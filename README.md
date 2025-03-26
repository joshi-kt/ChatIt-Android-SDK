# ChatIt Android SDK

Looking for a seamless way to integrate a chatbot into your application? Look no further! Incorporate Chat It to enjoy an effortless chatbot experience in your app, complete with audio-to-text support through audio file uploads.

## Screenshots

<p align="center">
  <img src="https://github.com/user-attachments/assets/6ee9f298-9320-4501-87e9-e1698044a698" width="200" height="400" />
  <img src="https://github.com/user-attachments/assets/936e4349-492a-4aed-87fa-36762cc69594" width="200" height="400" />
</p>


## Latest Releases

[![Maven Central](https://img.shields.io/maven-central/v/io.github.joshi-kt/chatIt-core.svg?label=Maven%20Central%20%7C%20chatIt-core)](https://search.maven.org/artifact/io.github.joshi-kt/chatIt-core)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.joshi-kt/chatIt-ui.svg?label=Maven%20Central%20%7C%20chatIt-ui)](https://central.sonatype.com/artifact/io.github.joshi-kt/chatIt-ui/overview)

## Integration Steps : 

 ### 1. Add mavenCentral to the project

   ``` gradle.kts
   dependencyResolutionManagement {
       repositories {
           mavenCentral()
       }
   }
   ```

### 2. For prebuilt UI

 - For gradle (kotlin)
   
   ``` gradle.kts
   implementation("io.github.joshi-kt:chatIt-core:${latest_version}")
   implementation("io.github.joshi-kt:chatIt-ui:${latest_version}")

- For gradle (groovy)

  ```gradle
  implementation 'io.github.joshi-kt:chatIt-core:${latest_version}'
  implementation 'io.github.joshi-kt:chatIt-ui:${latest_version}'

### 3. For self-handled UI

Just include the core dependency, and click [here](https://github.com/joshi-kt/ChatIt-Android-SDK/blob/main/chat-it/src/main/java/com/example/chat_it/helper/ChatItHelper.kt) for the helper methods.

## How to use it ?

### 1. Get your free Gemini API key by clicking [here](https://aistudio.google.com/apikey).

### 2. Initialize the ChatIt SDK using the below code :

 ```kotlin
 ChatIt(
     context = this,
     appKey = "${API_KEY}",
     localConfig = LocalConfig(),
     logConfig = LogConfig()
 ).initialize()
```

### 3. You can use the below configuration while initializing to modify the bot answers according to your use-case : 

``` kotlin
data class LocalConfig @JvmOverloads constructor(
    val queryPrompt : String = DEFAULT_PROMPT,
    @DrawableRes val botImage : Int = DEFAULT_IMAGE,
    val botName : String = DEFAULT_BOT_NAME,
    val botAudioQueryPrompt : String = DEFAULT_AUDIO_MESSAGE_OUTPUT_INSTRUCTION,
    val maxFileUploadSize : Int = MAX_FILE_SIZE
)

data class LogConfig @JvmOverloads constructor(
    val onDebugBuild : Boolean = true,
    val onReleaseBuild : Boolean = false
)
```

### 4. Start using the chatbot by using the below method : 

``` kotlin
ChatItUIHelper.startChat(context)
```
