package tw.edu.fgu.dclab.picpuzzle

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val image: ImageView = findViewById(R.id.imageView0);


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        image.setImageResource(R.drawable.fgu)
    }
}



