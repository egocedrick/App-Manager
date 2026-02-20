# App Manager

## Overview
An Android utility built with Kotlin for managing applications through guided dialogs and APK scanning. Designed to simplify uninstalling and reinstalling apps, the project demonstrates system-level integration and user-facing controls.  
Currently **under development**, with core features working and advanced logic in progress.

## Current Features
- **Auto-detect installed apps**  
- **Uninstall/Reinstall buttons** with guided dialogs  
- **APK scanning** in a default folder  
- **AlertDialog confirmations** for user actions  

## In Progress
- **Folder tracing logic refinement**  
  - Improve detection of APKs in multiple directories.  
- **Configurable folder path**  
  - Allow users to set custom locations for APK scanning.  
- **Improved uninstall targeting**  
  - Ensure the correct app is identified and removed.  

## Tech Stack
- **Language**: Kotlin  
- **Platform**: Android SDK  
- **System Features**: Package Manager, File I/O, Dialogs  

## Project Structure
- `/ui` – Activities and dialogs for app management  
- `/logic` – APK scanning, uninstall/reinstall operations  
- `/data` – (planned) folder path configuration and persistence  

## Setup Instructions
1. Install the application on the device.  
2. Launch the app to auto-detect installed applications.  
3. Use the **Uninstall/Reinstall** buttons to manage apps.  
4. APKs are scanned from the default folder (currently hardcoded).  

## Notes
- This project is a **work in progress** — uninstall/reinstall logic is not yet fully polished.  
- Demonstrates integration with Android’s Package Manager and file scanning.  
- Part of my mobile security and utility portfolio.  
