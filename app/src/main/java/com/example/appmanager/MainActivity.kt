package com.example.appmanager

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val targetPackage = "com.example.ticketingapp"
    private val PREFS_NAME = "AppManagerPrefs"
    private val KEY_FOLDER_PATH = "apkFolderPath"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = androidx.appcompat.widget.LinearLayoutCompat(this).apply {
            orientation = androidx.appcompat.widget.LinearLayoutCompat.VERTICAL
        }

        // Show uninstall only if app is installed
        if (isAppInstalled(targetPackage)) {
            val uninstallButton = Button(this).apply {
                text = "Uninstall Ticketing App"
                setOnClickListener { uninstallApp() }
            }
            layout.addView(uninstallButton)
        }

        val reinstallButton = Button(this).apply {
            text = "Reinstall Latest Ticketing App"
            setOnClickListener { reinstallLatestApk() }
        }

        val chooseFolderButton = Button(this).apply {
            text = "Choose APK Folder"
            setOnClickListener { chooseFolder() }
        }

        layout.addView(reinstallButton)
        layout.addView(chooseFolderButton)

        setContentView(layout)
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun uninstallApp() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$targetPackage")
        }
        startActivity(intent)
    }

    private fun reinstallLatestApk() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val apkFolderPath = prefs.getString(KEY_FOLDER_PATH, "/sdcard/Documents/") ?: "/sdcard/Documents/"

        val dir = File(apkFolderPath)
        val files = dir.listFiles { f -> f.extension == "apk" }

        if (files.isNullOrEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("No APK Found")
                .setMessage("No APK files found in:\n$apkFolderPath\nPlease copy the latest APK or re-select folder.")
                .setPositiveButton("Re-select Folder") { _, _ -> chooseFolder() }
                .setNegativeButton("OK", null)
                .show()
            return
        }

        var latestApk: File? = null
        var latestDate: Long = -1

        for (apk in files) {
            val info = packageManager.getPackageArchiveInfo(apk.path, 0)
            if (info?.packageName == targetPackage) {
                val modified = apk.lastModified()
                if (modified > latestDate) {
                    latestDate = modified
                    latestApk = apk
                }
            }
        }

        if (latestApk != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(latestApk.lastModified()))

            AlertDialog.Builder(this)
                .setTitle("Confirm Installation")
                .setMessage("Installing: ${latestApk.name}\nReceived: $formattedDate\nFrom folder: $apkFolderPath")
                .setPositiveButton("Proceed") { _, _ ->
                    val apkUri = FileProvider.getUriForFile(
                        this,
                        "${packageName}.provider",
                        latestApk
                    )
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(apkUri, "application/vnd.android.package-archive")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("No Matching APK")
                .setMessage("No APK for $targetPackage found in:\n$apkFolderPath\nDo you want to re-select folder?")
                .setPositiveButton("Re-select Folder") { _, _ -> chooseFolder() }
                .setNegativeButton("OK", null)
                .show()
        }
    }

    private fun chooseFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                val path = DocumentsContract.getTreeDocumentId(uri)
                val folderPath = "/sdcard/${path.substringAfter(":")}/"
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit()
                    .putString(KEY_FOLDER_PATH, folderPath)
                    .apply()

                Toast.makeText(this, "Folder set to: $folderPath", Toast.LENGTH_LONG).show()
            }
        }
    }
}