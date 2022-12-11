package com.example.puzzledroidkt

import android.view.LayoutInflater
import android.view.OnReceiveContentListener
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.puzzledroidkt.databinding.ViewPuzzleImageBinding

class PuzzleImagesAdapter(
    var puzzleImages: List<PuzzleImage>,
    private val puzzleClickedListener: (PuzzleImage) -> Unit
) :
    RecyclerView.Adapter<PuzzleImagesAdapter.ViewHolder>() {
    //View binding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewPuzzleImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }
    /*Normal inflater
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.view_puzzle_image, parent, false)

        return ViewHolder(view)
    }
    */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val puzzleImage = puzzleImages[position]
        holder.bind(puzzleImage)
        holder.itemView.setOnClickListener{puzzleClickedListener(puzzleImage)}
    }

    override fun getItemCount() = puzzleImages.size
    class ViewHolder(private val binding: ViewPuzzleImageBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(puzzleImage: PuzzleImage){
            Glide
                .with(binding.root.context)
                .load(puzzleImage.image)
                .override(104,132)
                .into(binding.image)
        }
    }
}