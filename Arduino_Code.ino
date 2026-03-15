#include <ESP8266WiFi.h>
#include <WiFiUdp.h>
#include "FirebaseESP8266.h"
#include <DateTime.h>

#define PROBE1 A0
#define PROBE2 A1
#define PROBE3 A2
#define PROBE4 A3

#define WATER_LEVEL 850

#define FIREBASE_HOST "*******************"
#define FIREBASE_AUTH "*************"
#define WIFI_SSID "*********"
#define WIFI_PASSWORD "**********"

FirebaseData fb;

int sensors[4];
String paths[4] = {
  "WaterLevel1",
  "WaterLevel2",
  "WaterLevel3",
  "WaterLevel4"
};

void connectWiFi()
{
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  Serial.print("Connecting");

  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }

  Serial.println();
  Serial.print("IP: ");
  Serial.println(WiFi.localIP());
}

void readSensors()
{
  sensors[0] = analogRead(PROBE1);
  sensors[1] = analogRead(PROBE2);
  sensors[2] = analogRead(PROBE3);
  sensors[3] = analogRead(PROBE4);
}

void sendFirebase()
{
  for(int i=0;i<4;i++)
  {
    Firebase.setInt(paths[i], sensors[i]);
  }
}

void setup()
{
  Serial.begin(115200);

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  connectWiFi();

  Firebase.reconnectWiFi(true);
}

void loop()
{
  readSensors();

  sendFirebase();

  delay(3000);
}