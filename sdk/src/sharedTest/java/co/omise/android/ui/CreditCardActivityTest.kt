package co.omise.android.ui

import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasFocus
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import co.omise.android.R
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreditCardActivityTest {

    private lateinit var scenario: ActivityScenario<CreditCardActivity>
    private val intent = Intent(InstrumentationRegistry.getInstrumentation().context, CreditCardActivity::class.java).apply {
        putExtra(OmiseActivity.EXTRA_PKEY, "test_key1234")
    }

    @Before
    fun setUp() {
        scenario = launch(intent)
    }

    @Test
    fun form_validForm() {
        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"), pressImeActionButton())

        onView(withId(R.id.edit_card_number)).check(matches(withText("4242 4242 4242 4242")))
        onView(withId(R.id.edit_card_name)).check(matches(withText("John Doe")))
        onView(withId(R.id.edit_expiry_date)).check(matches(withText("12/34")))
        onView(withId(R.id.edit_security_code)).check(matches(withText("123")))
        onView(withId(R.id.button_submit)).check(matches(isEnabled()))
    }

    @Test
    fun form_invalidForm() {
        onView(withId(R.id.edit_card_number)).perform(typeText("1234567890"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"), pressImeActionButton())

        onView(withId(R.id.edit_card_number)).check(matches(withText("1234 5678 90")))
        onView(withId(R.id.edit_card_name)).check(matches(withText("John Doe")))
        onView(withId(R.id.edit_expiry_date)).check(matches(withText("12/34")))
        onView(withId(R.id.edit_security_code)).check(matches(withText("123")))
        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }

    @Test
    fun forwardFocus_rightDirection() {
        onView(withId(R.id.edit_card_number)).perform(click(), pressImeActionButton())

        onView(withId(R.id.edit_card_name)).check(matches(hasFocus()))


        onView(withId(R.id.edit_card_name)).perform(pressImeActionButton())

        onView(withId(R.id.edit_expiry_date)).check(matches(hasFocus()))


        onView(withId(R.id.edit_expiry_date)).perform(pressImeActionButton())

        onView(withId(R.id.edit_security_code)).check(matches(hasFocus()))
    }

    @Test
    fun submitForm_disableFormWhenPressSubmit() {
        onView(withId(R.id.edit_card_number)).perform(typeText("4242424242424242"))
        onView(withId(R.id.edit_card_name)).perform(typeText("John Doe"))
        onView(withId(R.id.edit_expiry_date)).perform(typeText("1234"))
        onView(withId(R.id.edit_security_code)).perform(typeNumberText("123"), closeSoftKeyboard())
        onView(withId(R.id.button_submit)).perform(scrollTo(), click())

        onView(withId(R.id.edit_card_number)).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_card_name)).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_expiry_date)).check(matches(not(isEnabled())))
        onView(withId(R.id.edit_security_code)).check(matches(not(isEnabled())))
        onView(withId(R.id.button_submit)).check(matches(not(isEnabled())))
    }

    @Test
    fun backPressed_setResultCanceled() {
        pressBackUnconditionally()
        val result = scenario.result
        assertEquals(RESULT_CANCELED, result.resultCode)
    }
}

private fun typeNumberText(numberText: String): ViewAction =
        object : ViewAction {
            override fun getDescription(): String = "Type number text: $numberText"

            override fun getConstraints(): Matcher<View> =
                    allOf(isDisplayed(), isAssignableFrom(OmiseEditText::class.java))

            override fun perform(uiController: UiController?, view: View?) {
                val editText = view as? OmiseEditText ?: return
                numberText.forEach { editText.append(it.toString()) }
            }
        }
