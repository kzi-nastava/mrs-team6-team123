## Members:
1. Lana Mirkov SV23/2023
2. Ana Paroški SV53/2023
3. Aleksandar Papić SV80/2023

---

## How to run:

### 1. Requirements
- Java 17
- Maven
- Node.js
- Android Studio

### 2. Clone the repository
```bash
git clone https://github.com/kzi-nastava/mrs-team6-team123.git
cd mrs-team6-team123
```

### 3. Run backend
```bash
cd Projekatsiit2023/Projekatsiit2023
./mvnw spring-boot:run
```
Backend will run at: http://localhost:8080

### 4. Run frontend
```bash
npm install
cd client-layer
ng serve
```
Frontend will run at: http://localhost:4200

### 5. Run mobile app
Open mobile-application in Android Studio and run it on an emulator or an actual device<br>
Sync gradle

Backend base URL:<br>
- emulator: http://10.0.2.2:8080
- device: http://YOUR_LOCAL_IP:8080

Make sure the device you are running your backend and your mobile app on are connected to the same wifi 

Configuration:<br>
- ApiClient.java (ADDRESS)
- network_security_config.xml -> add: '<domain includeSubdomains="true"YOUR_LOCAL_IP</domain>'
