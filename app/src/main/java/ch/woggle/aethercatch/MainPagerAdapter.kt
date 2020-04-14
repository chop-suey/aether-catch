package ch.woggle.aethercatch

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ch.woggle.aethercatch.ui.capture.CaptureConfigurationFragment
import ch.woggle.aethercatch.ui.networks.NetworkListFragment
import java.lang.IndexOutOfBoundsException

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class MainPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private companion object {
        const val POSITION_NETWORKS = 0
        const val POSITION_CAPTURE_CONFIGURATION = 1
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            POSITION_NETWORKS -> NetworkListFragment.newInstance()
            POSITION_CAPTURE_CONFIGURATION -> CaptureConfigurationFragment.newInstance()
            else -> throw IndexOutOfBoundsException("No tab at specified position=$position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            POSITION_NETWORKS -> context.resources.getString(R.string.network_list_tab_title)
            POSITION_CAPTURE_CONFIGURATION -> context.resources.getString(R.string.capture_configuration_tab_title)
            else -> throw IndexOutOfBoundsException("No tab at specified position=$position")
        }
    }

    override fun getCount(): Int {
        return 2
    }
}