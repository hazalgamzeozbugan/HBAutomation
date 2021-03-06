package com.hepsiburada.step;

import com.hepsiburada.base.BaseTest;
import com.hepsiburada.model.ElementInfo;
import com.thoughtworks.gauge.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseSteps extends BaseTest {


    public static int DEFAULT_MAX_ITERATION_COUNT = 150;
    public static int DEFAULT_MILLISECOND_WAIT_AMOUNT = 100;

    private static String SAVED_ATTRIBUTE;

    private static String ELEMENT_TO_CONTROL;

    private String compareText;

    public BaseSteps() {
        initMap(getFileList());
    }

    WebElement findElement(String key) {
        By infoParam = getElementInfoToBy(findElementInfoByKey(key));
        WebDriverWait webDriverWait = new WebDriverWait(driver, 60);
        WebElement webElement = webDriverWait
                .until(ExpectedConditions.presenceOfElementLocated(infoParam));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})",
                webElement);
        return webElement;
    }

    List<WebElement> findElements(String key) {
        return driver.findElements(getElementInfoToBy(findElementInfoByKey(key)));
    }


    public By getElementInfoToBy(ElementInfo elementInfo) {
        By by = null;
        if (elementInfo.getType().equals("css")) {
            by = By.cssSelector(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("name"))) {
            by = By.name(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("classname"))) {
            by = By.className(elementInfo.getValue());
        } else if (elementInfo.getType().equals("id")) {
            by = By.id(elementInfo.getValue());
        } else if (elementInfo.getType().equals("xpath")) {
            by = By.xpath(elementInfo.getValue());
        } else if (elementInfo.getType().equals("linkText")) {
            by = By.linkText(elementInfo.getValue());
        } else if (elementInfo.getType().equals(("partialLinkText"))) {
            by = By.partialLinkText(elementInfo.getValue());
        }
        return by;
    }

    private void clickElement(WebElement element) {
        element.click();
    }

    private void clickElementBy(String key) {
        findElement(key).click();
    }

    private void hoverElement(WebElement element) {
        actions.moveToElement(element).build().perform();
    }


    private void hoverElementBy(String key) {
        WebElement webElement = findElement(key);
        actions.moveToElement(webElement).build().perform();
    }

    private boolean isDisplayedBy(By by) {
        return driver.findElement(by).isDisplayed();
    }


    public WebElement findElementWithKey(String key) {
        return findElement(key);
    }

    @Step("Get <key> text")
    public String getElementText(String key) {
        return findElement(key).getText();
    }


    @Step("<key> textini kay??t edilen text ile kar????la??t??r")
    public void compareText(String key) {
        boolean degisken = false;
        if (getElementText(key).equalsIgnoreCase(ELEMENT_TO_CONTROL)) {
            logger.info(getElementText(key) + " texti " + ELEMENT_TO_CONTROL + " texti ile kar????la??t??r??l??yor.");
            degisken = true;
        }

        assertTrue(degisken, "Kar????la??t??r??lan textler e??it de??il");

    }

    @Step({"Check if element <key> contains text <expectedText>",
            "<key> elementi <text> de??erini i??eriyor mu kontrol et"})
    public void
    checkElementContainsText(String key, String expectedText) {
        boolean kontrol = false;
        if (findElement(key).getText().equalsIgnoreCase(expectedText)) {
            kontrol = true;
        } else if (findElement(key).getText().contains(expectedText)) {
            kontrol = true;
        } else {
            logger.info(findElement(key).getText());
        }
        assertTrue(kontrol, "Expected text is not contained");
        logger.info(key + " elementi" + expectedText + "de??erini i??eriyor.");
    }

    public void javaScriptClicker(WebDriver driver, WebElement element) {

        JavascriptExecutor jse = ((JavascriptExecutor) driver);
        jse.executeScript("var evt = document.createEvent('MouseEvents');"
                + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
                + "arguments[0].dispatchEvent(evt);", element);
    }

    public void javascriptclicker(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }

    @Step({"Wait <value> seconds",
            "<int> saniye bekle"})
    public void waitBySeconds(int seconds) {
        try {
            logger.info(seconds + " saniye bekleniyor.");
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step({"Wait <value> milliseconds",
            "<long> milisaniye bekle"})
    public void waitByMilliSeconds(long milliseconds) {
        try {
            logger.info(milliseconds + " milisaniye bekleniyor.");
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Step({"Wait for element then click <key>",
            "Elementi bekle ve sonra t??kla <key>"})
    public void checkElementExistsThenClick(String key) {
        getElementWithKeyIfExists(key);
        clickElement(key);
        logger.info(key + " elementine t??kland??.");
    }


    @Step({"Click to element <key>",
            "Elementine t??kla <key>"})
    public void clickElement(String key) {
        if (!key.isEmpty()) {
            hoverElement(findElement(key));
            clickElement(findElement(key));
            logger.info(key + " elementine t??kland??.");
        }
    }

    @Step({"Check if element <key> exists",
            "Element var m?? kontrol et <key>"})
    public WebElement getElementWithKeyIfExists(String key) {
        WebElement webElement;
        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            try {
                webElement = findElementWithKey(key);
                logger.info(key + " elementi bulundu.");
                return webElement;
            } catch (WebDriverException e) {
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        assertFalse(Boolean.parseBoolean("Element: '" + key + "' doesn't exist."));
        return null;
    }


    @Step("<key> elementini kontrol et")
    public void checkElement(String key) {
        assertTrue(findElement(key).isDisplayed(), "Aranan element bulunamad??");
        if (!getElementText(key).isEmpty()) {
            logger.info(getElementText(key) + " de??erli element bulundu.");
        } else {
            logger.info(key + " elementi bulundu.");
        }
    }

    String FirstElement;
    String SecondElement;
    @Step("Sepetteki ilk ??r??n??n textini <key> ve ikinci ??r??n??n <key2> textini kar????la??t??r")
    public void compareElementText(String key,String key2) {
        FirstElement = findElement(key).getText();
        SecondElement =  findElement(key2).getText();
        logger.info("Kayit edilen element = " + key);
        logger.info("Kayit edilen element = " + key2);
        assertTrue(FirstElement.equals(SecondElement));

        /*if(FirstElement==SecondElement){
            logger.info("Sepetteki iki ??r??nde ayn??d??r!!");
        }
        else {
            logger.info("Sepetteki iki ??r??nde ayn?? de??il!!");
            logger.info("Kayit edilen ilk ??r??n = " + key);
            logger.info("Kayit edilen ikinci ??r??n = " + key2);
        }*/
    }

    @Step({"Go to <url> address",
            "<url> adresine git"})
    public void goToUrl(String url) {
        driver.get(url);
        logger.info(url + " adresine gidiliyor.");
    }

    @Step({"Check if element <key> exists else print message <message>",
            "Element <key> var m?? kontrol et yoksa hata mesaj?? ver <message>"})
    public void getElementWithKeyIfExistsWithMessage(String key, String message) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        By by = getElementInfoToBy(elementInfo);

        int loopCount = 0;
        while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
            if (driver.findElements(by).size() > 0) {
                logger.info(key + " elementi bulundu.");
                return;
            }
            loopCount++;
            waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
        }
        Assertions.fail(message);
    }

    @Step({"Write value <text> to element <key>",
            "<text> textini <key> elemente yaz"})
    public void ssendKeys(String text, String key) {
        if (!key.equals("")) {
            findElement(key).sendKeys(text);
            logger.info(key + " elementine " + text + " texti yaz??ld??.");
        }
    }

    @Step("popupa gec")
    public void switchTo() {
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
    }

    //Javascript driver??n ba??lat??lmas??
    private JavascriptExecutor getJSExecutor() {
        return (JavascriptExecutor) driver;
    }

    //Javascript scriptlerinin ??al????mas?? i??in gerekli fonksiyon
    private Object executeJS(String script, boolean wait) {
        return wait ? getJSExecutor().executeScript(script, "") : getJSExecutor().executeAsyncScript(script, "");
    }

    @Step({"<key> alan??na js ile kayd??r"})
    public void scrollToElementWithJs(String key) {
        ElementInfo elementInfo = findElementInfoByKey(key);
        WebElement element = driver.findElement(getElementInfoToBy(elementInfo));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    @Step("<key> elementine javascript ile t??kla")
    public void clickToElementWithJavaScript(String key) {
        WebElement element = findElement(key);
        javascriptclicker(element);
        logger.info(key + " elementine javascript ile t??kland??");
    }

    public void randomPick(String key) {
        List<WebElement> elements = findElements(key);
        Random random = new Random();
        int index = random.nextInt(elements.size());
        elements.get(index).click();
    }

    @Step("<key> menu listesinden rasgele se??")
    public void chooseRandomElementFromList(String key) {
        for (int i = 0; i < 100; i++)
            randomPick(key);
    }


    @Step("<key> elementiyle karsilastir")
    public void compareOrderStatus(String key) throws InterruptedException {
        WebElement cardDetail = findElement(key);
        String supplyDetailStatus = cardDetail.getText();
        logger.info(supplyDetailStatus + " texti bulundu");
        assertTrue(compareText.equals(supplyDetailStatus));
        logger.info(compareText + " textiyle " + supplyDetailStatus + " texti kar????la??t??r??ld??.");
    }

    @Step("<key> elementine <text> de??erini js ile yaz")
    public void writeToKeyWithJavaScript(String key, String text) {
        WebElement element = findElement(key);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].value=arguments[1]", element, text);
        logger.info(key + " elementine " + text + " de??eri js ile yaz??ld??.");
    }
    @Step({"Focus on tab number <number>",
            "<number> numaral?? sekmeye odaklan"})//Starting from 1
    public void chromeFocusTabWithNumber(int number) {
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(number - 1));
    }

}









