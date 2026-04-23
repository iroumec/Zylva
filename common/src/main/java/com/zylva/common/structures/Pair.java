package com.zylva.common.structures;

import java.io.Serializable;

/**
 *
 * @param first First element of the pair.
 * @param second Second element of the pair.
 * @param <T> First element's type.
 * @param <U> Second element's type.
 */
public record Pair<T, U>(T first, U second) implements Serializable {
}
