package co.omise.android;

public class PANTest extends SDKTest {
    public void testNormalize() {
        assertEquals("", PAN.normalize(null));
        assertEquals("4242424242424242", PAN.normalize("\r\r4242-4242-4242-4242  "));
        assertEquals("42424242", PAN.normalize("the42quick42brown42fox42jumps"));
    }

    public void testBrand() {
        assertEquals(CardBrand.VISA, PAN.brand("4242424242424242"));
        assertEquals(CardBrand.VISA, PAN.brand("4242-4242-4242-4242"));
        assertEquals(CardBrand.MASTERCARD, PAN.brand("5454545454545454"));
        assertEquals(CardBrand.JCB, PAN.brand("3566111111111113"));
    }

    public void testLuhn() {
        assertTrue(PAN.luhn("4242424242424242"));
        assertTrue(PAN.luhn("4242-4242-4242-4242"));
        assertFalse(PAN.luhn("4242424242424243"));
    }

    public void testIsValid() {
        assertTrue(PAN.isValid("4 2 4 2 4 2 4 2 4 2 4 2 4 2 4 2"));
        assertFalse(PAN.isValid("4242424242424243"));
        assertFalse(PAN.isValid("1234567812345687"));
    }
}
