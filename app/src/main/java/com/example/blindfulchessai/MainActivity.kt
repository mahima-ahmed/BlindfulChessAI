package com.example.blindfulchessai

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Locale

// Instrucciones que recibe la IA para describir el diagrama
private val PROMPT_DIAGRAMA = """
Eres un asistente de accesibilidad para personas ciegas que escuchan tu respuesta con un lector de pantalla o con síntesis de voz.
Recibirás la imagen de un diagrama (flujo, organigrama, esquema, mapa mental, red, etc.).
Escribe una descripción en español siguiendo estas reglas:
1. Usa solo texto plano: sin markdown, sin asteriscos, sin tablas y sin emojis.
2. La primera frase indica el tipo de diagrama, el tema y el número total de elementos. Ejemplo: Diagrama de flujo con 5 elementos sobre el registro de un usuario.
3. Recorre el diagrama en orden lógico: sigue el flujo, o de arriba abajo y de izquierda a derecha si no hay flujo. Usa frases cortas.
4. Indica siempre las relaciones y su sentido, por ejemplo: Inicio lleva a Introducir datos. En las decisiones indica cada rama y su etiqueta.
5. Escribe entre comillas los textos de los elementos tal como aparecen.
6. No inventes nada. Si algo es ilegible o dudoso, dilo al final en una línea que empiece por Aviso:
7. Si la imagen no es un diagrama, dilo en una frase y describe brevemente lo que ves.
8. Sé breve: unas 250 palabras, salvo que el diagrama sea muy grande.
""".trimIndent()

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    // Vistas
    private lateinit var layoutInicio: View
    private lateinit var layoutAnalisis: View
    private lateinit var layoutSeleccion: View
    private lateinit var layoutResultado: View
    private lateinit var imgTablero: ImageView
    private lateinit var txtPlaceholder: TextView
    private lateinit var txtResultado: TextView
    private lateinit var progreso: ProgressBar
    private lateinit var btnDescribir: Button
    private lateinit var btnAjedrez: Button
    private lateinit var btnGaleria: Button
    private lateinit var btnCamara: Button

    // Estado
    private var bitmapActual: Bitmap? = null
    private var uriFoto: Uri? = null
    private var textoActual: String = ""

    // Audio
    private var tts: TextToSpeech? = null
    private var ttsListo = false

    // IA de ajedrez (Teachable Machine)
    private val NOMBRE_MODELO = "model_unquant.tflite"
    private lateinit var clases: List<String>
    private var interpreter: Interpreter? = null

    // ---------- Selectores de imagen ----------

    private val elegirImagen =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { cargarDesdeUri(it) }
        }

    private val hacerFoto =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
            if (ok) uriFoto?.let { cargarDesdeUri(it) }
        }

    // ---------- Ciclo de vida ----------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutInicio = findViewById(R.id.layoutInicio)
        layoutAnalisis = findViewById(R.id.layoutAnalisis)
        layoutSeleccion = findViewById(R.id.layoutSeleccion)
        layoutResultado = findViewById(R.id.layoutResultado)
        imgTablero = findViewById(R.id.imgTablero)
        txtPlaceholder = findViewById(R.id.txtPlaceholder)
        txtResultado = findViewById(R.id.txtResultado)
        progreso = findViewById(R.id.progreso)
        btnDescribir = findViewById(R.id.btnDescribir)
        btnAjedrez = findViewById(R.id.btnAjedrez)
        btnGaleria = findViewById(R.id.btnGaleria)
        btnCamara = findViewById(R.id.btnCamara)

        tts = TextToSpeech(this, this)
        clases = cargarEtiquetas()
        cargarModelo()
        actualizarBotones()

        findViewById<Button>(R.id.btnComenzar).setOnClickListener {
            layoutInicio.visibility = View.GONE
            layoutAnalisis.visibility = View.VISIBLE
        }

        btnGaleria.setOnClickListener { elegirImagen.launch("image/*") }
        btnCamara.setOnClickListener { abrirCamara() }

        btnDescribir.setOnClickListener {
            bitmapActual?.let { describirDiagrama(it) }
        }

        btnAjedrez.setOnClickListener {
            bitmapActual?.let { analizarAjedrez(it) }
        }

        findViewById<Button>(R.id.btnEscuchar).setOnClickListener { escuchar() }
        findViewById<Button>(R.id.btnParar).setOnClickListener { tts?.stop() }

        findViewById<Button>(R.id.btnNueva).setOnClickListener {
            tts?.stop()
            bitmapActual = null
            textoActual = ""
            imgTablero.setImageDrawable(null)
            txtPlaceholder.visibility = View.VISIBLE
            layoutResultado.visibility = View.GONE
            layoutSeleccion.visibility = View.VISIBLE
            actualizarBotones()
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        interpreter?.close()
        super.onDestroy()
    }

    // ---------- Imagen ----------

    private fun abrirCamara() {
        val valores = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "diagrama_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores)
        if (uri == null) {
            Toast.makeText(this, "No se pudo preparar la cámara", Toast.LENGTH_LONG).show()
            return
        }
        uriFoto = uri
        hacerFoto.launch(uri)
    }

    private fun cargarDesdeUri(uri: Uri) {
        try {
            val origen = ImageDecoder.createSource(contentResolver, uri)
            val bmp = ImageDecoder.decodeBitmap(origen) { decoder, info, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                val maxLado = 1568
                val mayor = maxOf(info.size.width, info.size.height)
                if (mayor > maxLado) {
                    val f = maxLado.toFloat() / mayor
                    decoder.setTargetSize(
                        (info.size.width * f).toInt(),
                        (info.size.height * f).toInt()
                    )
                }
            }
            mostrarImagen(bmp)
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir la imagen", Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrarImagen(bmp: Bitmap) {
        bitmapActual = bmp
        imgTablero.setImageBitmap(bmp)
        txtPlaceholder.visibility = View.GONE
        actualizarBotones()
    }

    private fun actualizarBotones(cargando: Boolean = false) {
        val puedeAnalizar = bitmapActual != null && !cargando
        listOf(btnDescribir, btnAjedrez).forEach {
            it.isEnabled = puedeAnalizar
            it.alpha = if (puedeAnalizar) 1f else 0.5f
        }
        listOf(btnGaleria, btnCamara).forEach {
            it.isEnabled = !cargando
            it.alpha = if (cargando) 0.5f else 1f
        }
    }

    private fun mostrarResultado(texto: String) {
        textoActual = texto
        txtResultado.text = texto
        layoutSeleccion.visibility = View.GONE
        layoutResultado.visibility = View.VISIBLE
    }

    // ---------- MODO PRINCIPAL: describir diagrama con IA (Groq) ----------

    private fun describirDiagrama(bmp: Bitmap) {
        if (Config.API_KEY.startsWith("PEGA")) {
            mostrarResultado("Falta la clave de API. Ábrela en el archivo Config.kt y pégala ahí.")
            return
        }
        progreso.visibility = View.VISIBLE
        actualizarBotones(cargando = true)

        Thread {
            val texto = try {
                llamarGroq(bmp)
            } catch (e: Exception) {
                "No se pudo obtener la descripción. ${e.message}"
            }
            runOnUiThread {
                progreso.visibility = View.GONE
                actualizarBotones()
                mostrarResultado(texto)
            }
        }.start()
    }

    private fun llamarGroq(bmp: Bitmap): String {
        val salida = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 85, salida)
        val base64 = Base64.encodeToString(salida.toByteArray(), Base64.NO_WRAP)

        val contenidoUsuario = JSONArray()
            .put(
                JSONObject()
                    .put("type", "text")
                    .put("text", "Describe este diagrama siguiendo las reglas.")
            )
            .put(
                JSONObject()
                    .put("type", "image_url")
                    .put("image_url", JSONObject().put("url", "data:image/jpeg;base64,$base64"))
            )

        val mensajes = JSONArray()
            .put(JSONObject().put("role", "system").put("content", PROMPT_DIAGRAMA))
            .put(JSONObject().put("role", "user").put("content", contenidoUsuario))

        val cuerpo = JSONObject()
            .put("model", Config.MODELO)
            .put("messages", mensajes)
            .put("max_tokens", 3000)
            .put("temperature", 0.2)

        val conn = URL("https://api.groq.com/openai/v1/chat/completions").openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.connectTimeout = 20000
        conn.readTimeout = 90000
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json")
        conn.setRequestProperty("Authorization", "Bearer ${Config.API_KEY}")
        conn.setRequestProperty("User-Agent", "BlindfulChessAI/1.0")

        conn.outputStream.use { it.write(cuerpo.toString().toByteArray(Charsets.UTF_8)) }

        val codigo = conn.responseCode
        val flujo = if (codigo in 200..299) conn.inputStream else conn.errorStream
        val respuesta = flujo?.bufferedReader()?.use { it.readText() } ?: ""
        android.util.Log.d("GROQ_DEBUG", respuesta)

        if (codigo !in 200..299) {
            throw Exception("HTTP $codigo\n$respuesta")
            }

        val opciones = JSONObject(respuesta).optJSONArray("choices")
        var texto = opciones?.optJSONObject(0)?.optJSONObject("message")?.optString("content") ?: ""

        // Por si el modelo incluye su razonamiento interno entre etiquetas <think>
        texto = texto.replace(Regex("(?s)<think>.*?</think>"), "").trim()

        if (texto.isBlank()) throw Exception("La IA no devolvió texto. Prueba con otra imagen.")
        return texto
    }

    // ---------- MODO EXTRA: ajedrez con Teachable Machine ----------

    private fun cargarEtiquetas(): List<String> {
        return try {
            assets.open("labels.txt")
                .bufferedReader()
                .readLines()
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .map { it.replace(Regex("^\\d+\\s*"), "") }
        } catch (e: IOException) {
            listOf("tablero_vacio", "apertura_partida", "final_partida")
        }
    }

    private fun cargarModelo() {
        try {
            val bytes = assets.open(NOMBRE_MODELO).use { it.readBytes() }
            val buffer = ByteBuffer.allocateDirect(bytes.size).apply {
                order(ByteOrder.nativeOrder())
                put(bytes)
                rewind()
            }
            interpreter = Interpreter(buffer)
        } catch (e: Exception) {
            interpreter = null
        }
    }

    private fun analizarAjedrez(bitmap: Bitmap) {
        val tf = interpreter
        if (tf == null) {
            mostrarResultado("El modelo de ajedrez no está cargado.")
            return
        }
        try {
            val procesador = ImageProcessor.Builder()
                .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
                .add(NormalizeOp(127.5f, 127.5f))
                .build()

            val bitmapArgb = bitmap.copy(Bitmap.Config.ARGB_8888, false)
            var tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmapArgb)
            tensorImage = procesador.process(tensorImage)

            val bufferSalida = TensorBuffer.createFixedSize(
                intArrayOf(1, clases.size),
                DataType.FLOAT32
            )
            tf.run(tensorImage.buffer, bufferSalida.buffer.rewind())

            val prob = bufferSalida.floatArray
            var mejor = 0
            for (i in prob.indices) {
                if (prob[i] > prob[mejor]) mejor = i
            }
            val porcentaje = (prob[mejor] * 100).toInt()
            mostrarResultado(
                "Modo ajedrez. La inteligencia artificial detecta: ${clases[mejor]}. " +
                        "Seguridad: $porcentaje por ciento."
            )
        } catch (e: Exception) {
            mostrarResultado("Error al procesar la IA de ajedrez: ${e.message}")
        }
    }

    // ---------- Audio ----------

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {

            val r = tts?.setLanguage(Locale("es", "ES"))

            // Voz más natural
            tts?.setSpeechRate(0.9f)
            tts?.setPitch(1.0f)

            ttsListo =
                r != TextToSpeech.LANG_MISSING_DATA &&
                        r != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    private fun escuchar() {
        if (!ttsListo) {
            Toast.makeText(
                this,
                "La voz en español no está disponible en este dispositivo",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (textoActual.isBlank()) {
            Toast.makeText(
                this,
                "No hay texto para leer",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Detener cualquier lectura anterior
        tts?.stop()

        // Leer todo el texto de una vez
        tts?.speak(
            textoActual,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "descripcion_completa"
        )
    }
}