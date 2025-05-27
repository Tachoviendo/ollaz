# Ollaz - Olla Bruja Inteligente 🔥📱

Ollaz es una olla bruja inteligente desarrollada por el equipo ZLink como proyecto integrador de tecnologías. Está diseñada para optimizar la cocción pasiva de alimentos, reduciendo significativamente el consumo de energía, mientras el usuario monitorea la temperatura en tiempo real desde una aplicación Android.

## 🌱 ¿Qué es una Olla Bruja?

Es un sistema de cocción pasiva que mantiene el calor de una olla caliente por horas sin consumo adicional de energía. Ollaz mejora esta técnica tradicional mediante sensores, comunicación Bluetooth y una app móvil.

---

## 🧠 Tecnologías Utilizadas

- **Arduino Nano** (placa controladora)
- **Sensor DS18B20** (temperatura)
- **Bluetooth HC-05**
- **Kotlin** (desarrollo de la app)
- **Android Studio** (IDE usado para la app)
- **Figma** (diseño de interfaz)

---

## 📲 Características principales

- Monitoreo en tiempo real de la temperatura interior de la olla.
- Alertas cuando la temperatura se encuentra fuera del rango ideal (80–100 °C).
- Recetario integrado con tiempos estimados de cocción.
- App intuitiva conectada vía Bluetooth al módulo Arduino.

---

## 🛠️ Instalación y uso

### Aplicación móvil

Si solo querés usar la app:

- Descargá el APK desde el [repositorio](https://github.com/Tachoviendo/ollaz) o solicitálo directamente.

### Para desarrolladores

1. Cloná este repositorio:
   ```bash
   git clone https://github.com/Tachoviendo/ollaz.git
   ```
2. Abrí la carpeta del proyecto en **Android Studio** (recomendado para manejar dependencias).
3. Conectá tu dispositivo Android o usá un emulador.
4. Ejecutá la app.

### Arduino

- Subí el código del microcontrolador a tu **Arduino Nano**.
- Conectá el **sensor DS18B20** y el módulo **Bluetooth HC-05** como se indica en los esquemas del repositorio.

---

## 🧪 Prototipado

El proyecto fue diseñado, construido y probado bajo metodologías ágiles (Scrum y Design Thinking), incluyendo pruebas físicas del circuito, iteraciones sobre el diseño y entrevistas con potenciales usuarios.

---

## 📦 Estructura del repositorio

```
/ollaz
├── app/                  # Código fuente de la app Android
├── arduino/              # Código .ino del circuito Arduino
├── docs/                 # Diagramas, diseños, documentación técnica
├── apk/                  # APK instalable de la app
└── README.md
```

---

## 🚀 Pasos para continuar el proyecto.

- Migración de Bluetooth a WiFi para integración con hogares inteligentes.
- Creación de PCB dedicada.
- Fuente de energía recargable.
- Optimización de los materiales térmicos para mayor eficiencia.

---

## 👥 Autores

Proyecto desarrollado por el equipo **ZLink**:
- Ignacio Silva
- Renzo Beretta
- Lucca Di Raimundo
- Valentín Echeverría

Tutores:
- A. Borrero
- J. Di Laccio
- M. C. Durán

---

## 🪪 Licencia

Este proyecto es completamente libre. Podés modificar, distribuir y adaptarlo como desees.

> Si querés una licencia formal, se recomienda usar [MIT License](https://opensource.org/licenses/MIT) o [Creative Commons](https://creativecommons.org/licenses/by/4.0/).

---

## 📸 Redes y más info

Podés ver más sobre el proyecto en nuestro Instagram:  
🔗 [@zlink.ucu](https://www.instagram.com/zlink.ucu)

---

## 📁 Repositorio

https://github.com/Tachoviendo/ollaz
