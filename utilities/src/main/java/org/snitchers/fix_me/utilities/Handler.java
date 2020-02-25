package org.snitchers.fix_me.utilities;

import java.io.IOException;

public interface Handler<S> {
    void handle(S s) throws IOException;
}
