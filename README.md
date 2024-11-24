# TV_News_App 📺📰

An intuitive TV-friendly app that fetches and displays news headlines with seamless refresh functionality using DPAD controls. Built with **Kotlin** and **Jetpack Compose**, this app ensures a smooth and engaging user experience tailored for TV screens.

## Features 🌟

- **News API Integration**: Fetches real-time news headlines using a public API (e.g., [NewsAPI](https://newsapi.org)).
- **TV-Optimized Display**: A user-friendly interface designed specifically for 1080p TV screens.
- **Long Press DPAD-Down-to-Refresh**: Refresh news headlines with a long press on DPAD-Down.
- **Categories Support**: Easily switch between news categories.
- **Reliable Networking**: Handles errors like API limits or network issues gracefully using **Retrofit**.
- **Loading Indicators**: Provides visual feedback during data refresh operations.

## Screenshots 🖼️

### Headlines Screen
![Headlines Screen](https://github.com/AndroidLord/TV_News_App/blob/master/images/Headline%20ScreenShot.png)

### Business News
![Business News](https://github.com/AndroidLord/TV_News_App/blob/master/images/Buisness%20News.png)

## Demo Video 🎥

[Watch the demo here](https://drive.google.com/file/d/1f-4cYt02PgrrL8DiC2aQgKemKeP-zjfN/view?usp=sharing) to see the app in action!

---

## Setup Instructions ⚙️

Follow these steps to run the project locally:

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/AndroidLord/TV_News_App.git
   cd TV_News_App
   ```

2. **API Key Configuration**:
   - Obtain an API key from [NewsAPI](https://newsapi.org).
   - Add the API key in `utils`:
     ```
     API_KEY=your_api_key_here
     ```

3. **Build & Run**:
   - Open the project in Android Studio.
   - Select a TV emulator or connect a TV device.
   - Build and run the app.

---

## Key Libraries Used 📚

- **Kotlin**: For robust and concise code.
- **Jetpack Compose**: For modern UI development.
- **Retrofit**: For handling network requests.
- **Coil**: For loading images efficiently.

---

## How It Works 🔍

1. **Fetching News**:
   - News headlines are fetched via the News API using Retrofit.
   - Supports error handling for network issues and API failures.

2. **Displaying News**:
   - Headlines are displayed in a TV-friendly layout with easy readability.
   - Categories can be toggled for better personalization.

3. **Refreshing Data**:
   - A long press on DPAD-Down triggers a refresh action.
   - A loading indicator provides feedback during the refresh.

---

## Deliverables 📦

- **Source Code**: [GitHub Repository](https://github.com/AndroidLord/TV_News_App)
- **Demo Video**: [Drive Link](https://drive.google.com/file/d/1f-4cYt02PgrrL8DiC2aQgKemKeP-zjfN/view?usp=sharing)
- **Screenshots**: Available in the repository under `images/`.

---

## Evaluation Criteria ✅

The app has been built considering the following aspects:

- **Functionality**: Implements all specified requirements like news fetching and DPAD-Down refresh.
- **Code Quality**: Clean, maintainable, and well-documented.
- **User Experience**: Optimized for TVs, ensuring intuitive and smooth interactions.
- **Problem Solving**: Effectively handles API errors, network issues, and performance.
