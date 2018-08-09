package com.rosebay.odds.ui.singleOdd

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.rosebay.odds.util.Constants
import com.rosebay.odds.OddsApplication
import com.rosebay.odds.R
import com.rosebay.odds.model.SingleOdd
import com.rosebay.odds.util.SharedPreferencesClient
import com.squareup.picasso.Picasso
import easymvp.annotation.FragmentView
import easymvp.annotation.Presenter
import javax.inject.Inject

@FragmentView(presenter = SingleOddPresenterImpl::class)
class SingleOddFragment : Fragment(), SingleOddView {

    @Inject
    lateinit var sharedPreferencesClient: SharedPreferencesClient

    @Presenter
    lateinit var singleOddPresenter: SingleOddPresenterImpl

    @BindView(R.id.imageURLImageView)
    lateinit var mImageView: ImageView
    @BindView(R.id.descriptionSingleOdd)
    lateinit var mDescription: TextView
    @BindView(R.id.percentageSingleOdd)
    lateinit var mPercentage: TextView
    @BindView(R.id.singleOddCreationTextView)
    lateinit var mSingleOddCreationTextView: TextView
    @BindView(R.id.oddsForSingleOdd)
    lateinit var mOddsForTextView: TextView
    @BindView(R.id.oddsAgainstSingleOdd)
    lateinit var mOddsAgainstTextView: TextView
    @BindView(R.id.addToFavoritesButton)
    lateinit var mAddToFavoritesButton: ImageButton
    @BindView(R.id.voteYesButton)
    lateinit var mVoteYesButton: Button
    @BindView(R.id.voteNoButton)
    lateinit var mVoteNoButton: Button
    @BindView(R.id.voteLayout)
    lateinit var mVoteLayout: LinearLayout

    private var mSingleOdd: SingleOdd? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_single_odd, container, false)
        ButterKnife.bind(this, root)
        mSingleOdd = arguments?.getSerializable(Constants.SINGLE_ODD_KEY) as SingleOdd
        return root
    }

    override fun onAttach(context: Context) {
        OddsApplication.appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onResume() {
        super.onResume()
        singleOddPresenter.checkForFavorite(mSingleOdd!!.postId)
        singleOddPresenter.checkIfVoted(mSingleOdd!!.postId)
        singleOddPresenter.loadOddsData(mSingleOdd!!)
    }

    override fun onPause() {
        super.onPause()
        singleOddPresenter.onViewDetached()
    }

    override fun setPercentage(percentage: Int) {
        mPercentage.text = String.format(getString(R.string.percentage_text), percentage)
    }

    override fun setOddsFor(oddsFor: Int) {
        mOddsForTextView.text = oddsFor.toString()
    }

    override fun setOddsAgainst(oddsAgainst: Int) {
        mOddsAgainstTextView.text = oddsAgainst.toString()
    }

    override fun setImageUrl(imageUrl: String) {
        val uri = Uri.parse(imageUrl)
        Picasso.with(context).load(uri).fit().into(mImageView)
    }

    override fun setCreationInfo(username: String, creationDate: String) {
        mSingleOddCreationTextView.text = getString(R.string.created_by_date, username, creationDate)
    }

    override fun setDescription(description: String) {
        mDescription.text = description
    }

    override fun onAddedToFavorites() {
        Snackbar.make(mOddsAgainstTextView, R.string.added_to_favorites_msg, Snackbar.LENGTH_SHORT).show()
    }

    @OnClick(R.id.voteYesButton)
    fun voteYes() {
        singleOddPresenter.voteYes(mSingleOdd!!.postId)
    }

    @OnClick(R.id.voteNoButton)
    fun voteNo() {
        singleOddPresenter.voteNo(mSingleOdd!!.postId)
    }

    @OnClick(R.id.addToFavoritesButton)
    fun addToFavorites() {
        singleOddPresenter.addToFavorites(sharedPreferencesClient!!.getUsername(getString(R.string.username)), mSingleOdd!!.postId)
    }

    override fun onVoteSuccess() {
        Snackbar.make(mOddsAgainstTextView, R.string.vote_has_been_saved_msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun disableVoteButtons() {
        mVoteNoButton.isEnabled = false
        mVoteYesButton.isEnabled = false
    }

    override fun enableVoteButtons() {
        mVoteYesButton.isEnabled = true
        mVoteNoButton.isEnabled = true
    }

    override fun disableFavoritesButton() {
        mAddToFavoritesButton.isEnabled = false
        DrawableCompat.setTint(mAddToFavoritesButton.drawable, ContextCompat.getColor(context!!, R.color.accent))
    }

    override fun enableFavoritesButton() {
        mAddToFavoritesButton.isEnabled = true
        DrawableCompat.setTint(mAddToFavoritesButton.drawable, ContextCompat.getColor(context!!, R.color.primaryTextColor))
    }

    override fun onError() {
        Snackbar.make(mOddsAgainstTextView, R.string.bats_data_error, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity!!.supportFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()
        val refWatcher = OddsApplication.getRefWatcher(activity!!)
        refWatcher.watch(this)
    }

    companion object {

        fun newInstance(): SingleOddFragment {
            return SingleOddFragment()
        }
    }
}
