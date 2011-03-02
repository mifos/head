package org.mifos.platform.util;

public interface Transformer<I, O> {
    O transform(I input);
}
