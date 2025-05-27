package com.example.moviestream

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviestream.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var movieAdapter: MovieAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPlayer()
        loadMovies()
        setupRecyclerView()
    }

    private fun setupPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        binding.playerView.player = exoPlayer
    }

    private fun loadMovies() {
        db.collection("movies").get()
            .addOnSuccessListener { documents ->
                val movieList = documents.map { doc ->
                    Movie(
                        doc.id,
                        doc.getString("title") ?: "",
                        doc.getString("thumbnail") ?: "",
                        doc.getString("streamUrl") ?: ""
                    )
                }
                movieAdapter.submitList(movieList)
            }
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter { movie ->
            playMovie(movie.streamUrl)
        }
        binding.moviesRecyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = movieAdapter
        }
    }

    private fun playMovie(streamUrl: String) {
        val mediaItem = MediaItem.fromUri(Uri.parse(streamUrl))
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
        binding.playerView.visibility = android.view.View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}
