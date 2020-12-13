package cipher;

import cipher.test_cases.TestCeasar;
import cipher.test_cases.TestXOR;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        TestCeasar.class,
        TestXOR.class
})
public class CipherSuite {

}
