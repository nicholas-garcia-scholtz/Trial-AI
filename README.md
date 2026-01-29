# Sample JavaFX application using Proxy API

## To setup the API to access Chat Completions and TTS

- add in the root of the project (i.e., the same level where `pom.xml` is located) a file named `apiproxy.config`
- put inside the credentials your email and an OpenAi access token

  ```
  email: "example@gmail.com"
  apiKey: "OpenAI_Access_Token"
  ```
  These are your credentials to invoke the APIs. 

## To run the game

`./mvnw clean javafx:run`

## To debug the game

`./mvnw clean javafx:run@debug` then in VS Code "Run & Debug", then run "Debug JavaFX"
