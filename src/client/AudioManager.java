package client;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;

//배경 음악(BGM)과 효과음(발사음)을 관리
//클립(Clip) 객체를 사용해 오디오 파일을 재생
public class AudioManager {
    private Clip bgmClip; //// 배경 음악(BGM) 재생을 위한 Clip 객체
    private Clip shotClip;  // 발사 효과음 재생을 위한 Clip 객체
    private boolean isMuted = false; // 음소거 상태를 나타내는 플래그


    // BGM 재생 메서드
    public void playBGM() {
        try {
            // BGM 로드
            // 오디오 파일을 InputStream으로 로드 (리소스 경로: /client/assets/sounds/bgm.wav)
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    new BufferedInputStream(getClass().getResourceAsStream("/client/assets/sounds/bgm.wav"))
            );
            // Clip 객체 생성 및 오디오 데이터 열기
            bgmClip = AudioSystem.getClip();
            bgmClip.open(audioInputStream);

            // 음소거 상태가 아니면 재생
            if (!isMuted) {
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // BGM 정지 메서드
    public void stopBGM() {
        if (bgmClip != null) {
            bgmClip.stop();
            bgmClip.close();
        }
    }

    // 발사 효과음 재생 메서드
    public void playShotSound() {
        // 음소거 상태면 효과음 재생 안함
        if (isMuted) return;

        try {
            // 발사 효과음 로드
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    new BufferedInputStream(getClass().getResourceAsStream("/client/assets/sounds/shot.wav"))
            );

            // Clip 객체 생성 및 오디오 데이터 열기
            shotClip = AudioSystem.getClip();
            shotClip.open(audioInputStream);
            shotClip.start(); // 한 번 재생
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // 음소거 토글 메서드
    public boolean toggleMute() {
        isMuted = !isMuted;

        if (isMuted) {
            // BGM 정지
            if (bgmClip != null) {
                bgmClip.stop();
            }
        } else {
            // BGM 재생
            if (bgmClip != null) {
                bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }

        return isMuted;
    }

    // 음소거 상태 확인 메서드
    public boolean isMuted() {
        return isMuted;
    }
}