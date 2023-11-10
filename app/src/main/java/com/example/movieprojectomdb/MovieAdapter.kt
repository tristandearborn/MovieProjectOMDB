import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieprojectomdb.R
import com.example.movieprojectomdb.movie


class MovieAdapter(private val context: Context) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() { // creates list of movies provided

    private val movies: MutableList<movie> = mutableListOf()

    fun setMovies(movieList: List<movie>) { //creates set of movie list
        movies.clear()
        movies.addAll(movieList)
        notifyDataSetChanged()
    }

    fun clearMovies() { //clears movie list
        movies.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder { //creates a viewholder with movie_item xml
        val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) { //uses data from movie.kt to hold all important information
        val movie = movies[position]

        holder.titleTextView.text = movie.Title
        holder.yearTextView.text = "Year: ${movie.Year ?: "N/A"}"
        holder.ratingTextView.text = "Rating: ${movie.Ratings ?: "N/A"}"
        holder.runtimeTextView.text = "Runtime: ${movie.Runtime ?: "N/A"}"
        holder.genreTextView.text = "Genre: ${movie.Genre ?: "N/A"}"
        holder.imdbRatingTextView.text = "IMDb Rating: ${movie.imdbRating ?: "N/A"}"
        holder.imdbLinkTextView.text = "Click Here to Open IMDb Link!"


        Glide.with(holder.itemView.context) //initiates glide holder for poster image
            .load(movie.Poster)
            .into(holder.posterImageView)


        holder.imdbLinkTextView.setOnClickListener { //creates a link to the movie
            openIMDbPage(movie.imdbID)
        }


        holder.shareButton.setOnClickListener { // a share button that takes imdb movie title and id
            shareMovie(movie.Title, "https://www.imdb.com/title/${movie.imdbID}/")
        }
    }

    override fun getItemCount(): Int { //gets count of items in movie list
        return movies.size
    }

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //adds all data collected from movie.kt to the recyclerview
        val titleTextView: TextView = itemView.findViewById(R.id.movieTitleTextView)
        val posterImageView: ImageView = itemView.findViewById(R.id.posterImageView)
        val yearTextView: TextView = itemView.findViewById(R.id.yearTextView)
        val ratingTextView: TextView = itemView.findViewById(R.id.ratingTextView)
        val runtimeTextView: TextView = itemView.findViewById(R.id.runtimeTextView)
        val genreTextView: TextView = itemView.findViewById(R.id.genreTextView)
        val imdbRatingTextView: TextView = itemView.findViewById(R.id.imdbRatingTextView)
        val imdbLinkTextView: TextView = itemView.findViewById(R.id.imdbLinkTextView)
        val shareButton: Button = itemView.findViewById(R.id.shareButton)
    }

    private fun openIMDbPage(imdbID: String) { //when open imdb page clicked, creates and launches intent to movie page on imdb website
        val imdbUri = Uri.parse("https://www.imdb.com/title/$imdbID/")
        val imdbIntent = Intent(Intent.ACTION_VIEW, imdbUri)
        context.startActivity(imdbIntent)
    }

    private fun shareMovie(title: String, imdbUrl: String) { //when share button clicked, creates a message with title and link to share and uses a share intent
        val message = "'$title' on IMDb: $imdbUrl"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
}
