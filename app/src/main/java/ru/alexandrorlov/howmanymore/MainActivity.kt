package ru.alexandrorlov.howmanymore

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.alexandrorlov.howmanymore.config.Config.MILLISECONDS_IN_ONE_DAY
import ru.alexandrorlov.howmanymore.config.Config.ONE_YEAR
import ru.alexandrorlov.howmanymore.model.Bar
import ru.alexandrorlov.howmanymore.model.Screen
import ru.alexandrorlov.howmanymore.model.Sex
import ru.alexandrorlov.howmanymore.model.User
import ru.alexandrorlov.howmanymore.ui.theme.HowManyMoreTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val density = LocalDensity.current.density.toInt()
            val screen = getScreen(
                screenHeight = LocalConfiguration.current.screenHeightDp * density,
                screenWidth = LocalConfiguration.current.screenWidthDp * density,
                density = density
            )

            Log.d("OAE", "screenWidthDp = ${screen.width}")
            Log.d("OAE", "screenHeightDp = ${screen.height}")
            Log.d("OAE", "density = ${screen.density}")

            HowManyMoreTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier,
                    color = MaterialTheme.colors.background
                ) {
                    val birthday = Calendar.getInstance()
                    //TODO месяц считается от нуля
                    birthday.set(1996, 0, 3)
                    val yearLived = getYearLived(
                        User(
                            Sex.MALE, birthday, "ru"
                        )
                    )
                    val yearLifeExpectancy = getYearLifeExpectancy(
                        User(
                            Sex.MALE, birthday, "ru"
                        )
                    )
                    val ratio = getRatio(yearLived, yearLifeExpectancy)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Blue)
                    ) {
                        Greeting(
                            stringResource(id = R.string.year_lived) + " " + yearLived,
                            rect = getRectLived(screen, ratio),
                            colorRect = Color.White,
                            colorText = Color.Black
                        )
                        Greeting(
                            stringResource(id = R.string.year_life_expectancy) + " " + yearLifeExpectancy,
                            rect = getRectYearLifeExpectancy(screen, ratio),
                            colorRect = Color.Black,
                            colorText = Color.White
                        )
                    }
                }
            }
        }
    }

    private fun getScreen(screenHeight: Int, screenWidth: Int, density: Int): Screen {
        return Screen(
            screenHeight, screenWidth, density, Bar(), Bar()
        )
    }

    private fun getYearLifeExpectancy(user: User): Float {
        //TODO берём из User sex и country и делаем запрос к базе данных
        return 50.0f
    }

    private fun getYearLived(user: User): Float {
        val toDay = Calendar.getInstance().time
        val birthday = user.birthday
        val difference = toDay.time.minus(birthday.time.time)
        val daysLived = difference / MILLISECONDS_IN_ONE_DAY
        return daysLived / ONE_YEAR
    }

    private fun getRatio(yearLived: Float, yearLifeExpectancy: Float): Float {
        return yearLived / yearLifeExpectancy
    }

    private fun getRectLived(screen: Screen, ratio: Float): Rect {
        val topLeft = Offset(0f, 0f)
        val bottomRight = Offset(screen.width.toFloat(), ratio * screen.height)

        return Rect(topLeft = topLeft, bottomRight = bottomRight)
    }

    private fun getRectYearLifeExpectancy(screen: Screen, ratio: Float): Rect {
        val topLeft = Offset(0f, ratio * screen.height)
        val bottomRight = Offset(screen.width.toFloat(), screen.height.toFloat())

        return Rect(topLeft = topLeft, bottomRight = bottomRight)
    }
}

@Composable
fun Greeting(
    name: String,
    rect: Rect,
    colorRect: Color,
    colorText: Color
) {
    MeasureUnconstrainedViewWidth(
        viewToMeasure = { Text(text = name) }
    ) { measuredWidth ->
        Log.d("OAE", "measuredWidth = $measuredWidth")

    }
    val paint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        textSize = 48f
        color = colorText.hashCode()
        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
    }
    Canvas(modifier = Modifier)
    {
        drawRect(
            color = colorRect,
            topLeft = rect.topLeft,
            size = rect.size
        )
        drawIntoCanvas {
            //TODO по иксу надо считать
            it.nativeCanvas.drawText(name, 0f, rect.center.y, paint)
        }
    }
//    Log.d("OAE", "Greeting name = $name")
//    Log.d("OAE", "rect topLeft $name = ${rect.topLeft}")
//    Log.d("OAE", "rect bottomRight $name = ${rect.bottomRight}")
//    Log.d("OAE", "rect size $name = ${rect.size}")
//    Log.d("OAE", "center = ${rect.size.center}")
}

@Composable
fun MeasureUnconstrainedViewWidth(
    viewToMeasure: @Composable () -> Unit,
    content: @Composable (measuredWidth: Dp) -> Unit,
) {
    SubcomposeLayout { constraints ->
        val measuredWidth = subcompose("viewToMeasure", viewToMeasure).first()
            .measure(Constraints()).width.toDp()

        val contentPlaceable = subcompose("content") {
            content(measuredWidth)
        }.first().measure(constraints)
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}