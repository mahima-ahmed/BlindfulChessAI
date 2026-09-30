# ♟️ BlindfulChess AI

**BlindfulChess AI** is an Android application that combines **artificial intelligence, computer vision, and voice-based interaction** to analyze chess boards and provide accessible feedback for blind and visually impaired users.

The project was developed as part of an **AWS Hackathon**, with the goal of exploring how AI and mobile technology can be combined to create a more accessible chess experience.

The application analyzes chess board images using **TensorFlow Lite** and integrates **Groq** for AI-powered text generation and voice interaction.

---

## 📱 About the Project

BlindfulChess AI allows users to provide an image of a chess board and receive an AI-generated description of the position.

The application supports both:

* 📁 Selecting an existing image from the device.
* 📷 Taking a new photograph using the device camera.

After analyzing the image, the application can recognize the chess pieces and describe their positions on the board, allowing users to understand the chess position through audio-based interaction.

### Key capabilities

* Analyze chess board images using artificial intelligence.
* Upload an existing chess board image from the device.
* Take a photograph of a chess board directly with the mobile camera.
* Recognize chess pieces from the provided image.
* Identify the positions of the detected pieces on the chess board.
* Generate a description of the analyzed chess position.
* Provide AI-powered voice interaction and feedback.
* Display the detected information and model confidence.

The project was designed with **accessibility as a central consideration**, particularly for blind and visually impaired users who cannot directly view a physical chess board.

---

## 🧠 Artificial Intelligence

The project uses machine learning and image analysis to interpret chess board images.

The image classification model was created and trained using **Google Teachable Machine** and exported as a TensorFlow Lite model.

The current classification model recognizes three game-state categories:

```text
tablero_vacio
apertura_partida
final_partida
```

The image-analysis workflow is:

```text
Chess board image
        ↓
Image preprocessing
        ↓
TensorFlow Lite model
        ↓
Image classification / recognition
        ↓
Chess board information
        ↓
AI-generated description
        ↓
Voice-based feedback
```

The TensorFlow Lite model is included in:

```text
app/src/main/assets/model_unquant.tflite
```

The class labels are stored in:

```text
app/src/main/assets/labels.txt
```

---

## ♟️ Chess Board Recognition

One of the main features of BlindfulChess AI is its ability to analyze the content of a chess board image.

Depending on the provided image, the application can identify the chess pieces and their positions on the board and generate a description of the resulting position.

For example, the system can provide information about:

```text
White pieces
Black pieces
Piece type
Piece position
Current board configuration
```

This information can then be presented through the application's AI-powered interaction, helping a blind or visually impaired user understand the board without needing to see it.

---

## 🗣️ Groq Integration

BlindfulChess AI integrates the **Groq API** to support AI-powered text generation and voice-related functionality.

The AI-generated information can be transformed into a form that is easier to understand through spoken feedback.

### API Key Setup

For security reasons, **never publish a real Groq API key in the repository**.

The configuration file should contain a placeholder:

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
| **TensorFlow Lite**          | On-device machine learning and image analysis      |
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

Configure your Groq API key locally:

```kotlin
object Config {
    const val API_KEY = "YOUR_GROQ_API_KEY"
    const val MODELO = "qwen/qwen3.8-27b"
}
```

Then build and run the application on an Android emulator or compatible physical device.

---

## 🔍 How the Application Works

The user begins by providing a chess board image.

The application can use:

```text
Existing image
      OR
Mobile camera photograph
```

The image is then processed by the AI system.

The general workflow is:

```text
User selects or captures an image
              ↓
       Image processing
              ↓
      AI / ML recognition
              ↓
     Chess piece detection
              ↓
   Piece position identification
              ↓
   Chess board description
              ↓
      AI text generation
              ↓
       Voice feedback
```

This allows the user to receive an audio-friendly description of the chess board.

---

## 🖼️ Image Input

BlindfulChess AI supports two ways of providing a chess board image:

### 📁 Upload an image

The user can select an existing photograph or image stored on the device.

### 📷 Take a photograph

The user can use the mobile device's camera to photograph a physical chess board and submit the image for analysis.

This makes the application usable in different real-world situations without requiring a pre-existing digital image.

---

## 🎯 Accessibility

Accessibility is a central part of the BlindfulChess AI concept.

A traditional chess board is highly visual, which creates a significant barrier for blind users. This project explores how computer vision and voice interaction can transform visual information into accessible descriptions.

The application can provide information about:

* The pieces present on the board.
* The color of each piece.
* The position of each piece.
* The overall configuration of the chess board.
* A spoken description of the analyzed position.

The goal is to allow users to understand a chess position through **audio rather than relying exclusively on vision**.

---

## 🎯 Project Purpose

BlindfulChess AI was created for an **AWS Hackathon** as a practical exploration of:

* Artificial intelligence
* Computer vision
* Android development
* Machine learning
* Voice interaction
* Accessibility technology
* AI-assisted chess analysis

The project demonstrates how different AI technologies can be combined to build a mobile application focused on accessibility.

---

## 🔮 Possible Future Enhancements

Although the hackathon project is complete, several features could further improve BlindfulChess AI:

### ⠿ Braille Support

A future version could support **Braille transcription of chess positions and descriptions**, allowing users with Braille displays or other compatible devices to access the same information through Braille in addition to voice feedback.

### ♟️ More Advanced Chess Analysis

The application could be expanded to provide deeper chess analysis, including:

* Legal move detection.
* Suggested moves.
* Position evaluation.
* FEN generation.
* Integration with chess engines.

### 🗣️ More Natural Voice Interaction

Future versions could provide a more conversational voice interface, allowing users to ask questions about the detected chess position and receive spoken answers.

### 🤖 Improved Recognition

The recognition model could be trained with a larger and more diverse dataset containing different:

* Chess boards
* Lighting conditions
* Camera angles
* Piece designs
* Image backgrounds

This could improve the robustness of the recognition system in real-world situations.

---

## 📚 What I Learned

Through this project, I gained practical experience with:

* Android application development using Kotlin.
* Android Studio and Gradle.
* TensorFlow Lite integration.
* Image preprocessing for machine learning.
* Deploying machine learning models on Android.
* Computer vision concepts.
* API integration.
* AI-powered text and voice interaction.
* Designing applications with accessibility in mind.
* Git and GitHub version control.
* Debugging Android build and dependency issues.
* Combining multiple technologies into a functional application.

---

## 🏆 AWS Hackathon

**BlindfulChess AI was developed as an AWS Hackathon project.**

The project explores how artificial intelligence and mobile technologies can be applied to improve accessibility in chess and create an alternative way for blind and visually impaired users to understand chess positions.

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

## ✅ Project Status

**Completed**

BlindfulChess AI is a completed AWS Hackathon project demonstrating the integration of an Android application, machine learning-based chess board recognition, image and camera input, and AI-powered voice interaction.

The project can serve as a foundation for further development in **accessible chess technology and AI-powered assistive applications**.

---

## 📄 License

No open-source license has been specified yet.
