package coil.sample

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bfView = findViewById<ImageView>(R.id.bitmapfactory)
        val coilView = findViewById<ImageView>(R.id.coil)
        val imageLoader= ImageLoader.Builder(this)
            .dispatcher(Dispatchers.IO)
            .build()

        lifecycleScope.launch {
            val (nativeBitmap, coilBitmap) =
                getBitmap(R.drawable.instagram_icon, this@MainActivity, null) to
                fetchBitmap(R.drawable.instagram_icon, this@MainActivity, imageLoader, Size.ORIGINAL)

//            val (nativeBitmap, coilBitmap) =
//                BitmapFactory.decodeStream(this@MainActivity.assets.open("instagram_icon.png")) to
//                fetchBitmap("file:///android_asset/instagram_icon.png", this@MainActivity, imageLoader, Size.ORIGINAL)
            bfView.setImageBitmap(nativeBitmap)
            coilView.setImageBitmap(coilBitmap)
        }
    }

    fun getBitmap(@DrawableRes id: Int, context: Context, color: Int?): Bitmap {
        // If resource is a png drawable.
        BitmapFactory.decodeResource(
            context.resources, id
        )?.let {
            return it
        }


        // If resource is a xml drawable.
        val drawable = AppCompatResources.getDrawable(context, id)
            ?: throw Resources.NotFoundException("Could not find drawable with ID '$id'")
        color?.let { drawable.setTint(it) }
        return drawable.toBitmap()
    }

    suspend fun fetchBitmap(
        data: Any,
        context: Context,
        imageLoader: ImageLoader,
        size: Size
    ): Bitmap = withContext(Dispatchers.IO) {
        val imageRequest = ImageRequest.Builder(context)
            .data(data)
            .size(size)
            .allowHardware(false)
            .build()
        val drawable = imageLoader.execute(imageRequest).drawable
            ?: throw Resources.NotFoundException("Could not find drawable with ID '$data'")

        if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            drawable.toBitmap()
        }
    }
}
