package me.jigulsw.api.domain.code;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class ResponseStatusTest {

    @Test
    public void 코드값보이기() {
        Arrays.stream(ResponseStatus.values()).forEach(
                it -> System.out.println(it.name())
        );
    }

}