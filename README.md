# ♟️ BlindfulChess AI

**BlindfulChess AI** is an Android application that combines **artificial intelligence, computer vision, and voice-based interaction** to analyze chess boards and provide accessible feedback.

The project was developed as part of an **AWS Hackathon**, with a focus on exploring how AI and mobile technology can be combined to create a more accessible chess experience.

The application uses a **TensorFlow Lite machine learning model** to classify chess board images and **Groq** to support AI-powered text and voice interaction.

---

## 📱 About the Project

BlindfulChess AI provides an accessible mobile interface for analyzing chess board images.

The completed application can:

* Analyze chess board images using a TensorFlow Lite model.
* Classify the board into different game-state categories.
* Display the detected category and confidence score.
* Provide a dedicated Android interface for starting the analysis.
* Integrate Groq for AI-powered text generation and voice-related functionality.

The project demonstrates the integration of **machine learning, Android development, and AI APIs** within a single mobile application.

---

## 🧠 Artificial Intelligence

The image classification model was created and trained using **Google Teachable Machine** and exported as a TensorFlow Lite model.

The model recognizes three categories:

```text
tablero_vacio
apertura_partida
final_partida
```

The image-processing workflow is:

```text
Chess board image
        ↓
Image preprocessing
        ↓
Resize to 224 × 224
        ↓
TensorFlow Lite model
        ↓
Prediction probabilities
        ↓
Highest-confidence class
        ↓
Result displayed in the app
```

The model is included in the project under:

```text
app/src/main/assets/model_unquant.tflite
```

The class labels are stored in:

```text
app/src/main/assets/labels.txt
```

---

## 🗣️ Groq Integration

BlindfulChess AI also integrates the **Groq API** for AI-powered text generation and voice-related functionality.

### API Key Setup

For security reasons, **never publish a real Groq API key in the repository**.

The configuration file should contain:

```kotlin
object Config {
    const val API_KEY = "YOUR_GROQ_API_KEY_HERE"
    const val MODELO = "qwen/qwen3.8-27b"
}
```

To run the project locally, replace the placeholder with your own Groq API key.

> ⚠️ Never commit a real API key, token, password, or other private credential to GitHub.

---

## 🛠️ Technologies Used

| Technology                   | Purpose                                            |
| ---------------------------- | -------------------------------------------------- |
| **Kotlin**                   | Android application development                    |
| **Android Studio**           | Development environment                            |
| **TensorFlow Lite**          | On-device image classification                     |
| **Google Teachable Machine** | Machine learning model training                    |
| **Groq API**                 | AI-powered text generation and voice functionality |
| **Material Components**      | Android user interface                             |
| **Gradle**                   | Build and dependency management                    |

---

## 📂 Project Structure

```text
BlindfulChessAI/
│
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/example/blindfulchessai/
│   │       │       ├── MainActivity.kt
│   │       │       └── Config.kt
│   │       │
│   │       ├── res/
│   │       │   ├── drawable/
│   │       │   ├── layout/
│   │       │   ├── mipmap/
│   │       │   ├── values/
│   │       │   └── xml/
│   │       │
│   │       ├── assets/
│   │       │   ├── model_unquant.tflite
│   │       │   ├── labels.txt
│   │       │   └── test_tablero.jpg
│   │       │
│   │       └── AndroidManifest.xml
│   │
│   └── build.gradle.kts
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── .gitignore
└── README.md
```

---

## 🚀 Getting Started

### Requirements

To build and run the project, you will need:

* Android Studio
* Android SDK
* A physical Android device or Android Emulator
* A valid Groq API key for the Groq-powered functionality

### Installation

Clone the repository:

```bash
git clone https://github.com/mahima-ahmed/BlindfulChessAI.git
```

Open the project in Android Studio and allow Gradle to synchronize the project.

Add your own Groq API key to the local configuration:

```kotlin
object Config {
    const val API_KEY = "YOUR_GROQ_API_KEY"
    const val MODELO = "qwen/qwen3.8-27b"
}
```

Then build and run the application on an Android emulator or compatible physical device.

---

## 🔍 How the Chess Analysis Works

When the user starts an analysis, the application:

1. Loads the chess board image.
2. Resizes it to `224 × 224` pixels.
3. Converts it into a TensorFlow Lite-compatible tensor.
4. Runs inference using the bundled model.
5. Reads the prediction probabilities.
6. Determines the class with the highest confidence.
7. Displays the result to the user.

Example:

```text
IA Detecta: apertura_partida (94% de acierto)
```

---

## 🖼️ Current Implementation

The application includes a test image:

```text
test_tablero.jpg
```

stored in the application's assets and used to demonstrate the complete image-analysis pipeline.

The project successfully integrates the trained model with the Android application and displays the resulting classification and confidence score.

---

## 🎯 Project Purpose

BlindfulChess AI was created for an **AWS Hackathon** as a practical exploration of:

* Artificial intelligence
* Computer vision
* Android development
* Voice interaction
* Accessibility
* Machine learning model deployment

The project demonstrates how different AI technologies can be combined into a functional mobile application.

---

## 🔮 Possible Future Enhancements

Although the hackathon project is complete, the concept could be expanded with additional features such as:

* 📷 Direct camera-based chess board scanning.
* ♟️ Individual chess piece recognition.
* 🧩 Automatic reconstruction of a chess position.
* 🔊 More advanced voice feedback.
* ♟️ Chess move and position analysis.
* 📱 Further accessibility and UI improvements.
* 🤖 Training the model with a larger and more diverse dataset.

These are potential extensions of the project rather than unfinished features of the current version.

---

## 📚 What I Learned

Through this project, I gained practical experience with:

* Android application development using Kotlin.
* Android Studio and Gradle.
* TensorFlow Lite integration.
* Image preprocessing for machine learning.
* Deploying machine learning models on Android.
* Integrating external AI APIs.
* AI-powered voice interaction.
* Git and GitHub version control.
* Debugging Android build and dependency issues.
* Combining multiple technologies into a functional application.

---

## 👩‍💻 Author

**Mahima Ahmed**

Computer Science & Engineering student interested in:

* Software Development
* Artificial Intelligence
* Machine Learning
* Mobile Development
* Computer Networks

GitHub: [github.com/mahima-ahmed](https://github.com/mahima-ahmed)

---

## 🏆 Hackathon Project

**Developed for an AWS Hackathon**

This project was created as part of a hackathon challenge focused on building a functional technology solution using modern cloud, AI, and software development technologies.

---

## ✅ Project Status

**Completed**

BlindfulChess AI is a completed hackathon project demonstrating the integration of an Android application, an on-device TensorFlow Lite model, and AI-powered voice functionality.
