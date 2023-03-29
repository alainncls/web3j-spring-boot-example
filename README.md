# Web3j Spring Boot example

This project aims to demonstrate the [web3j](https://docs.web3j.io) library in a [Spring Boot](https://spring.io/projects/spring-boot) application.

## How to use

1. Add a `secrets.properties` file in `src/main/resources`:
   ```
   touch src/main/resources/secrets.properties
   ```
2. Paste your Infura API key in this newly created file:
    ```
    infuraApiKey=SECRET_API_KEY 
    ```
3. Start the server:
   ```
   ./gradlew bootRun
   ```
4. You should get a series of logs:
   ```
   Block #16933655 with hash = 0xc6ca72fc387e972cad706dc9722e9fb3cab7d73c59a14b28b2e185a0ef82be02 contains 237 transactions
   Block #16933655 - Transfer 78282.759936 USDT from 0x5050e08626c499411b5d0e0b5af0e83d3fd82edf to 0x11b815efb8f581194ae79006d24e0d814b7697f6
   Block #16933655 - Transfer 55 USDT from 0x671c13474da46f2f3acf1dcab7e29d3693a752a7 to 0x5aa065c88342413c9c68b9b86d80ab2f3e359d2a
   Block #16933655 - Transfer 1890 USDT from 0x7563758243a262e96880f178aee7817dcf47ab0f to 0x1e96de23d11a4e23a6eca6a9f10ba0c5c926da6e
   ```
