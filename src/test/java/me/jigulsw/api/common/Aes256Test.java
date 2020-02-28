package me.jigulsw.api.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class Aes256Test {

    @Test
    public void 암복화화_테스트() {
        String plain = "hello world!!";
        String enc = Aes256.encrypt(plain);
        String dec = Aes256.decrypt(enc);

        log.info("plain [{}]", plain);
        log.info("enc [{}]", enc);
        log.info("dec [{}]", dec);

        assertThat(dec).isEqualTo(plain);
    }

    @Test
    public void 암복화화_테스트_사용자IV사용() {
        String plain = "hello world!!";
        String iv = "happybirthday 2013.11.21";
        String enc = Aes256.encrypt(plain, iv);
        String dec = Aes256.decrypt(enc, iv);

        log.info("plain [{}]", plain);
        log.info("enc [{}]", enc);
        log.info("dec [{}]", dec);

        assertThat(dec).isEqualTo(plain);
    }

}