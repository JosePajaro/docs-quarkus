package org.acme;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.acme.GreetingResourceTest;

@QuarkusIntegrationTest
class GreetingResourceIT extends GreetingResourceTest {
    // Execute the same tests but in packaged mode.
}
