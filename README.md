# 🚌 Opita GO - Tu Guía Inteligente del Transporte en Neiva

**Opita GO** es una aplicación nativa de Android, construida desde cero con Kotlin y las últimas tecnologías recomendadas por Google. El proyecto nació para resolver un problema real en Neiva, Huila: la falta de una herramienta digital, moderna y fácil de usar para navegar el Sistema Estratégico de Transporte Público (SETP).

Esta aplicación no solo sirve como una guía de rutas, sino también como un proyecto de portafolio que demuestra un dominio completo del desarrollo de aplicaciones Android modernas, desde la arquitectura de software hasta la experiencia de usuario final.

---

### 🎥 Demostración en Vivo (GIF)



https://github.com/user-attachments/assets/3d178503-13f9-4a88-a618-cbb05b69d6d5



*En esta demostración se observa: la lista de rutas agrupadas, el buscador inteligente funcionando en tiempo real, la navegación a la pantalla de detalle, la visualización de las rutas en el mapa y el cálculo de proximidad.*

---

## ✨ Características Principales

* **Arquitectura Moderna MVVM:** Implementación limpia del patrón Model-View-ViewModel, separando la lógica de la UI y facilitando la mantenibilidad y escalabilidad del código.
* **Base de Datos Local con Room:** Toda la información de las rutas se almacena localmente, permitiendo un acceso ultra rápido y el funcionamiento completo de la app incluso sin conexión a internet.
* **Programación Reactiva con Flow y Corrutinas:** La UI se actualiza de forma reactiva a los cambios en la base de datos y a las acciones del usuario, utilizando Corrutinas de Kotlin para un manejo eficiente de las tareas en segundo plano.
* **Lista de Rutas Agrupada:** La pantalla principal presenta las rutas de forma clara y profesional, agrupando visualmente los trayectos de "Ida" y "Vuelta" en una sola tarjeta por cada número de ruta.
* **Buscador Inteligente:** Un potente buscador que permite filtrar rutas en tiempo real por:
    * Número de Ruta (ej: "247")
    * Nombre de la tarjeta (ej: "Las Palmas")
    * Cualquier barrio o punto de interés en la descripción completa del recorrido.
    * La búsqueda ignora mayúsculas y minúsculas para una mejor experiencia de usuario.
* **Mapa Interactivo con Google Maps SDK:**
    * Visualización de múltiples polilíneas (Ida en verde, Vuelta en rojo) en el mismo mapa.
    * Ajuste de cámara automático (`LatLngBounds`) para encuadrar todas las rutas visibles.
    * Hoja de información inferior (`BottomSheet`) con la descripción detallada del recorrido.
* **Geolocalización y Permisos:**
    * Implementación del flujo de solicitud de permisos de ubicación en tiempo de ejecución, siguiendo las mejores prácticas de Android.
    * Muestra la ubicación actual del usuario ("punto azul") en el mapa.
    * **Funcionalidad Clave:** Un Botón de Acción Flotante (FAB) que calcula y muestra al usuario el punto exacto de la ruta más cercano a su ubicación actual, indicando la distancia en metros.

---

## 📱 Capturas de Pantalla

| Lista Principal y Búsqueda | Detalle de Ruta y Proximidad |
| :-------------------------: | :--------------------------: |

![Captura desde 2025-06-29 12-19-26](https://github.com/user-attachments/assets/3224b649-1ae3-4328-9459-9dceb857398b)
![Captura desde 2025-06-29 12-19-52](https://github.com/user-attachments/assets/4d5b3462-20e2-4bc5-b4ed-11cf8ff2ada4)
![Captura desde 2025-06-29 12-20-09](https://github.com/user-attachments/assets/e25b71f1-c0bb-445e-8cab-2f177af27e72)


---

## 🛠️ Stack Tecnológico

* **Lenguaje:** **Kotlin** (100%)
* **Arquitectura:** **MVVM** (Model-View-ViewModel)
* **Componentes de Android Jetpack:**
    * **UI:** Vistas con XML, `RecyclerView`, `MaterialCardView`, `FloatingActionButton`, `TextInputLayout`.
    * **Datos:** **Room** (Persistencia local), **StateFlow** (Flujos de datos reactivos).
    * **Ciclo de Vida:** `ViewModel`, `lifecycleScope`.
    * **Fragmentos:** `SupportMapFragment`.
* **Manejo de Asincronía:** **Corrutinas de Kotlin**.
* **Mapas:** **Google Maps Platform** (Maps SDK for Android).
* **Dependencias:** Catálogo de Versiones de Gradle (`libs.versions.toml`).

---

## 🚀 Cómo Ejecutar el Proyecto

Para clonar y ejecutar este proyecto en tu propia máquina, sigue estos pasos:

1.  **Clona el repositorio:**
    ```bash
    git clone [https://github.com/tu_usuario/OpitaGO.git](https://github.com/tu_usuario/OpitaGO.git)
    ```
2.  **Abre el proyecto** en la última versión estable de Android Studio.
3.  **Obtén una API Key de Google Maps:**
    * Ve a la [Google Cloud Console](https://console.cloud.google.com/).
    * Crea un proyecto nuevo.
    * En el menú de APIs y Servicios, busca y habilita **"Maps SDK for Android"**.
    * Ve a "Credenciales", crea una "Clave de API" y asegúrate de restringirla para evitar usos no autorizados (recomiendo añadir una restricción de aplicación para Android con el nombre de tu paquete y tu firma SHA-1).
4.  **Añade tu API Key:**
    * En el proyecto, busca y abre el archivo `app/src/main/AndroidManifest.xml`.
    * Dentro de la etiqueta `<application>`, busca la línea `<meta-data android:name="com.google.android.geo.API_KEY" ... />`.
    * Reemplaza `"TU_API_KEY_AQUI"` con tu clave real.
5.  **Construye y Ejecuta** la aplicación en un emulador o dispositivo físico.

