# LEDGER 💾

A small application that exposes a simple ledger API. Built with Micronaut.

---

## Running instructions 🏃

### 1️⃣ Make sure you have
- **JDK 21+**
- **Gradle 9.x**
- **Git**

### 2️⃣ Get the code
```bash
git clone https://github.com/gui-tr/ledger-service.git
cd ledger-service
```

### 3️⃣ Build & run the app 🚀
```bash
# Build the project 
./gradlew build

# Run the app
./gradlew run
```

## API overview 🌐
**Default Micronaut Base URL:** `http://localhost:8080`

| Method | Endpoint                                      | Description                                 |
|--------|-----------------------------------------------|---------------------------------------------|
| **GET**    | `/ledger/accounts`                              | Get all accounts and their balances         |
| **POST**   | `/ledger/accounts`                              | Open a new account                          |
| **DELETE** | `/ledger/accounts/{account}`                    | Delete an existing account                  |
| **GET**    | `/ledger/accounts/{account}/balance`            | Retrieve the balance of an account          |
| **POST**   | `/ledger/accounts/{account}/deposit`            | Deposit money into an account               |
| **GET**    | `/ledger/accounts/{account}/transactions`       | Get transaction history for an account      |
| **POST**   | `/ledger/accounts/{account}/withdrawal`         | Withdraw money from an account              |
| **POST**   | `/ledger/transfer`                              | Transfer money between two accounts         |


## Execute features 🤖

### Option 1: Access swagger API docs (recommended)
Go to `http://localhost:8080/swagger-ui`

### Option 2: Use curl commands
```bash
# Open new account
curl -X POST "http://localhost:8080/ledger/accounts?baseCcy=USD"

# Delete an account
curl -X DELETE "http://localhost:8080/ledger/accounts/{account}"

# Get account balance
curl -s "http://localhost:8080/ledger/accounts/{account}/balance" | jq

# Get account transactions history
curl -s "http://localhost:8080/ledger/accounts/{account}/transactions" | jq

# Get all accounts and their balances
curl -s "http://localhost:8080/ledger/accounts" | jq

# Deposit money into account
curl -X POST "http://localhost:8080/ledger/accounts/{account}/deposit?amount=100.00&currency=USD" | jq

# Withdraw money from account
curl -X POST "http://localhost:8080/ledger/accounts/{account}/withdrawal?amount=50.00" | jq

# Transfer money between accounts
curl -X POST "http://localhost:8080/ledger/transfer?fromAccount={account}&toAccount={otherAccount}&amount=25.00" | jq
```




## Micronaut DOCS

### Micronaut 4.10.2 Documentation
- [User Guide](https://docs.micronaut.io/4.10.2/guide/index.html)
- [API Reference](https://docs.micronaut.io/4.10.2/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/4.10.2/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

- [Shadow Gradle Plugin](https://gradleup.com/shadow/)
- [Micronaut Gradle Plugin documentation](https://micronaut-projects.github.io/micronaut-gradle-plugin/latest/)
- [GraalVM Gradle Plugin documentation](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)
  
### Feature serialization-jackson documentation
- [Micronaut Serialization Jackson Core documentation](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/)

### Feature micronaut-aot documentation
- [Micronaut AOT documentation](https://micronaut-projects.github.io/micronaut-aot/latest/guide/)
