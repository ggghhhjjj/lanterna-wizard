# Lanterna Wizard

**Lanterna Wizard** is a text-based wizard interface implemented in **Groovy** using the [Lanterna](https://github.com/mabe02/lanterna) library. It demonstrates a multi-screen wizard flow with a clear separation of UI zones, each built using the **builder design pattern**. This project is ideal for learning how to build terminal-based user interfaces with a modular and object-oriented approach.

## Features

- **Multi-Screen Flow**  
  Navigate between wizard screens using "Next", "Back", "Finish", and "Exit" buttons.
- **Three Distinct UI Zones per Screen**
    - **Description Zone:** Magenta background, white border, and black text.
    - **Question Zone:** A question prompt and input text field with a default answer.
    - **Wizard Buttons Zone:** Navigation buttons (Exit, Next/Finish, Back).
- **Builder Pattern**
    - Each UI zone is encapsulated in its own class.
    - Zones are composed into the main screen via a builder design pattern.
- **Gradle-Based Project**
    - Uses Gradle for dependency management and project execution.

## 📁 Project Structure


- **`Main.groovy`** – Entry point, initializes wizard screens.
- **`WizardScreen.groovy`** – Assembles the screen layout (blue background) and integrates the three UI zones.
- **`DescriptionZone.groovy`** – Displays the description with a **magenta background** and a **white border**.
- **`QuestionZone.groovy`** – Provides a question prompt with an input text field.
- **`WizardButtonsZone.groovy`** – Contains the navigation buttons (**Exit, Next/Finish, Back**).

## 🛠 Prerequisites

- **JDK 8 or higher**
- **Gradle** (or use the included Gradle wrapper)

## 🚀 Setup and Running

### 1️⃣ Clone the Repository

```sh
git clone https://github.com/your-username/lanterna-wizard.git
cd lanterna-wizard
```

### 2️⃣ Build the Project

If you have Gradle installed:

```sh
gradle build
```

Or use the Gradle wrapper:

```sh
./gradlew build   # Linux / macOS
gradlew build     # Windows
```
### 3️⃣ Run the Application

```sh
gradle run
```
Or with the wrapper:

```sh
./gradlew run   # Linux / macOS
gradlew run     # Windows
```
### ✨ Customization
* Theming & Styling
    - Lanterna 3.1.1 does not support direct color or focus manipulation on buttons.
    - To customize button colors or tab order, explore Lanterna’s theming options.
* Extending the Wizard
    - Add more screens by creating additional WizardScreen instances.
    - Chain the navigation logic (onNext, onBack) between them.

### 📜 License

This project is released under the MIT License. See [LICENSE](LICENSE) for details.


💡 Contributions are welcome! If you find issues or improvements, feel free to submit a pull request.
