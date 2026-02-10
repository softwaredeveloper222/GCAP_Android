package com.gcap.main.chartsGraphs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gcap.R
import com.gcap.core.models.ChartItem
import com.squareup.picasso.Picasso

import java.io.File
import java.io.FileOutputStream
import java.net.URL

import java.io.BufferedInputStream
import java.net.HttpURLConnection

class ChartsAdapter(
    private val chartsList: List<ChartItem>,
    private val onItemClick: (ChartItem) -> Unit
) :
    RecyclerView.Adapter<ChartsAdapter.ValveViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValveViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_charts, parent, false)
        return ValveViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ValveViewHolder, position: Int) {
        val item = chartsList[position]
        holder.bind(item)

        if (item.image.endsWith(".pdf", ignoreCase = true)) {
            Thread {
                val fileName = item.image.substringAfterLast("/")
                val localFile = downloadPdf(
                    holder.imageView.context,
                    "https://gcapcoolworks.com/" + item.image,
                    fileName
                )

                val bitmap = localFile?.let { generatePdfThumbnail(it) }

                holder.itemView.post {
                    if (bitmap != null) {
                        holder.imageView.setImageBitmap(bitmap)
                    }
                }
            }.start()
        } else {
            Picasso.get()
                .load("https://gcapcoolworks.com/" + item.image)
                .into(holder.imageView)
        }
    }

    override fun getItemCount(): Int = chartsList.size

    class ValveViewHolder(itemView: View, private val onItemClick: (ChartItem) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvAnimation)
        val imageView: ImageView = itemView.findViewById(R.id.ivAnimation)

        fun bind(item: ChartItem) {
            nameTextView.text = item.name

            Picasso.get()
                .load("https://gcapcoolworks.com/" + item.image)
                .into(imageView)


            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }
}

fun generatePdfThumbnail(file: File): Bitmap? {
    return try {
        val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(fd)
        val page = renderer.openPage(0)

        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        page.close()
        renderer.close()
        fd.close()

        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun downloadPdf(context: Context, urlString: String, fileName: String): File? {
    return try {
        val dir = context.getExternalFilesDir(null) ?: return null
        val file = File(dir, fileName)
        if (file.exists()) file.delete()

        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 15000
        connection.readTimeout = 15000
        connection.doInput = true
        connection.connect()

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            connection.disconnect()
            return null
        }

        val inputStream = BufferedInputStream(connection.inputStream)
        val outputStream = FileOutputStream(file)

        val buffer = ByteArray(8192)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()
        connection.disconnect()

        if (file.length() == 0L) return null

        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
