NatureConnect - Android Application  
Student Name: Owen Reid  
Student Number: 2201368  
Submission: CMP309

GITHUB LINK: https://github.com/wawabing/natureconnect

Please note that the github only holds the app files.
The APK and firebase rules are only supplied in the submitted ZIP file.

---

## 🎥 Presentation Video
LINK: https://youtu.be/p5GhzY9xscY

---

## 📱 About the App
NatureConnect is a nature-focused social media app designed to promote engagement with the natural world. 
Users can share nature content, manage a digital garden, and ask questions to an AI nature chatbot. 
The app supports multiple languages and accessibility options for a more inclusive experience.

---

## Installation & Usage Instructions

1. **Installation:**
   - Install the provided APK file (`nature-connect.apk`) on your Android device. 

2. **Usage:**
   - **Login or Register** to begin using the app.
   - **Feed:** View and post nature content.
   - **Garden:** Add and view plants with AI-generated information.
   - **NatureBot:** Ask nature-related questions.
   - **Profile:** Set profile picture, language, and accessibility settings.

3. **NatureBot:**
   - the OPENAI API key has been taken out, replace with your own for it to work, in **feature_naturebot/data/RetrofitClient.kt** on line 14
   
---

## ⚙️ Technical Details
- Built using Kotlin with Jetpack Compose and MVVM architecture.
- Firebase used for authentication and Firestore for data storage.
- OpenAI API integrated for chatbot and plant info generation.
- App supports RTL languages and handles orientation changes gracefully.

---

