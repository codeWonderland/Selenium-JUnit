import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.openqa.selenium.firefox.FirefoxDriver
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SeleniumUnitTest {
    companion object {
        const val pathFirefox = "./selenium-java/geckodriver"
        var driver: FirefoxDriver? = null
    }

    init {
        // Establish Driver Location
        System.setProperty("webdriver.gecko.driver", pathFirefox)
    }

    @BeforeEach
    fun initDriver() {
        // Create Driver
        driver = FirefoxDriver()

        // Load Site
        driver!!.get("https://www.saucedemo.com/")

        // Enter Username
        enterText("//*[@id=\"user-name\"]", "standard_user")

        // Enter Password
        enterText("//*[@id=\"password\"]", "secret_sauce")

        // Click Login Button
        clickButton("/html/body/div[2]/div[1]/div[1]/div/form/input[3]")
    }

    @Test
    @DisplayName("Add Item to Cart")
    fun addToCart() {
        // Add item to cart
        clickButton("/html/body/div/div[2]/div[2]/div/div[2]/div/div[1]/div[3]/button")

        // Go To Checkout
        goToCheckout()

        // Verify Total
        verifyTotal("32.39")
    }

    @Test
    @DisplayName("Remove Item From Cart")
    fun removeFromCart() {
        // Add item one to cart
        clickButton("/html/body/div/div[2]/div[2]/div/div[2]/div/div[1]/div[3]/button")

        // Add second item to cart
        clickButton("/html/body/div/div[2]/div[2]/div/div[2]/div/div[3]/div[3]/button")

        // Remove second item from cart
        clickButton("/html/body/div/div[2]/div[2]/div/div[2]/div/div[3]/div[3]/button")

        // Go To Checkout
        goToCheckout()

        // Verify Total
        verifyTotal("32.39")
    }

    @Test
    @DisplayName("Filter Products by Low to High Price")
    fun filterProducts() {
        // Open Filter Menu
        clickButton("/html/body/div/div[2]/div[2]/div/div[1]/div[3]/select")

        // Select Low to High Price Filter
        clickButton("/html/body/div/div[2]/div[2]/div/div[1]/div[3]/select/option[3]")

        // Check that first element is $7.99
        assertText("/html/body/div/div[2]/div[2]/div/div[2]/div/div[1]/div[3]/div", "\$7.99")
    }

    @Test
    @DisplayName("Verify Number of Items in Cart")
    fun verifyNumItems() {
        // Add item one to cart
        clickButton("/html/body/div/div[2]/div[2]/div/div[2]/div/div[1]/div[3]/button")

        // Add second item to cart
        clickButton("/html/body/div/div[2]/div[2]/div/div[2]/div/div[3]/div[3]/button")

        // Check the number of items in the cart
        assertText("/html/body/div/div[2]/div[1]/div[2]/a/span", "2")
    }

    @Test
    @DisplayName("Verify Total = Subtotal + Tax")
    fun verifyCorrectTotal() {
        // Add item one to cart
        clickButton("/html/body/div/div[2]/div[2]/div/div[2]/div/div[1]/div[3]/button")

        // Add second item to cart
        clickButton("/html/body/div/div[2]/div[2]/div/div[2]/div/div[3]/div[3]/button")

        // Go To Checkout
        goToCheckout()

        // Grab Subtotal and Tax
        val subtotal = getPrice("/html/body/div/div[2]/div[3]/div/div[2]/div[5]").toDouble()
        val tax = getPrice("/html/body/div/div[2]/div[3]/div/div[2]/div[6]").toDouble()

        // Verify Total
        verifyTotal("${subtotal + tax}")
    }

    @AfterEach
    fun cleanup() {
        // Open Menu
        clickButton("/html/body/div/div[1]/div/div[3]/div/button")

        // Clear Session Storage
        driver!!.sessionStorage.clear()

        // Reset App State
        clickButton("//*[@id=\"logout_sidebar_link\"]")

        // Close Window
        driver!!.close()
    }

    private fun verifyTotal(expected: String) {
        // Grab Total Price
        val actual = getPrice("/html/body/div/div[2]/div[3]/div/div[2]/div[7]")

        // Verify Price is as Expected
        assertEquals(expected, actual)
    }

    private fun getPrice(xPath: String): String {
        // Get text from price field
        val fullPrice = getText(xPath)

        // Return the numerical price
        // Stripped of unnecessary data
        return fullPrice.split("$")[1]
    }

    private fun getText(xPath: String): String {
        // Get reference to item
        val currentField = driver!!.findElementByXPath(xPath)

        // Return Text
        return currentField.text
    }

    private fun goToCheckout() {
        // Go To Cart
        clickButton("/html/body/div/div[2]/div[1]/div[2]/a")

        // Checkout
        clickButton("/html/body/div/div[2]/div[3]/div/div[2]/a[2]")

        // Enter First Name
        enterText("//*[@id=\"first-name\"]", "Test")

        // Enter Last Name
        enterText("//*[@id=\"last-name\"]", "User")

        // Enter Zip Code
        enterText("//*[@id=\"postal-code\"]", "05401")

        // Click Continue
        clickButton("/html/body/div/div[2]/div[3]/div/form/div[2]/input")
    }

    private fun assertText(xPath: String, expected: String) {
        // Grab Requested Field
        val currentField = driver!!.findElementByXPath(xPath)

        // Assert Text is as Expected
        assertEquals(expected, currentField.text)
    }

    private fun enterText(xPath: String, input: String) {
        // Grab Reference to text field
        val currentField = driver!!.findElementByXPath(xPath)

        // Enter input into field
        currentField.sendKeys(input)
    }

    private fun clickButton(xPath: String) {
        // Grab reference to button
        val currentButton = driver!!.findElementByXPath(xPath)

        // Click button
        currentButton.click()
    }
}