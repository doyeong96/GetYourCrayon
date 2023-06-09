package com.sevenight.coldcrayon.game.controller;

import com.sevenight.coldcrayon.auth.dto.UserDto;
import com.sevenight.coldcrayon.auth.service.AuthService;
import com.sevenight.coldcrayon.game.dto.GameRequestDto;
import com.sevenight.coldcrayon.game.dto.ImgDto;
import com.sevenight.coldcrayon.game.dto.RequestRoundDto;
import com.sevenight.coldcrayon.game.dto.ResponseRoundDto;
import com.sevenight.coldcrayon.game.service.GameService;
import com.sevenight.coldcrayon.game.service.SaveImageServiceImpl;
import com.sevenight.coldcrayon.game.service.WebClientServiceImpl;
import com.sevenight.coldcrayon.room.entity.RoomHash;
import com.sevenight.coldcrayon.room.repository.RoomRepository;
import com.sevenight.coldcrayon.theme.entity.ThemeCategory;
import com.sevenight.coldcrayon.util.HeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {

    final GameService gameService;
    final AuthService authService;
    final WebClientServiceImpl webClientService;
    final SaveImageServiceImpl saveImageService;
    final RoomRepository roomRepository;

    @PostMapping("/start")
    public ResponseEntity<?> gameStart(@RequestHeader String Authorization, @RequestBody GameRequestDto gameRequestDto) throws IOException {
        // 에러는 못 던진다.....
        System.err.println("start 컨트롤러 진입");
        UserDto user = authService.selectOneMember(HeaderUtil.getAccessTokenString(Authorization));

        return ResponseEntity.ok().body(gameService.startGame(user, gameRequestDto));
    }


    @PostMapping("/nextRound")
    public ResponseEntity<?> getKeyword(@RequestHeader String Authorization, @RequestBody RequestRoundDto requestRoundDto) throws IOException {
        UserDto user = authService.selectOneMember(HeaderUtil.getAccessTokenString(Authorization));
        return ResponseEntity.ok().body(gameService.nextRound(requestRoundDto));
    }



    @PostMapping("/end-round")
    public ResponseEntity<ResponseRoundDto> endRound(@RequestHeader String Authorization, @RequestBody RequestRoundDto requestRoundDto){
        ResponseRoundDto responseRoundDto = gameService.endRound(requestRoundDto);

        return ResponseEntity.ok().body(responseRoundDto);
    }


    @PostMapping("/end-game")
    public ResponseEntity<?> endGame(){
        return null;
    }

    @PostMapping("/saveImg")
    public void saveImg(@RequestHeader String Authorization, @RequestParam("img") String img, @RequestParam("roomIdx") String roomIdx) throws IOException {

        Optional<RoomHash> roomHashOptional = roomRepository.findById(roomIdx);

        if(roomHashOptional.isPresent()) {
            RoomHash roomHash = roomHashOptional.get();
            String destinationPath = roomHash.getRoomIdx()+"/" + roomHash.getGameCnt() +"/"+ roomHash.getNowRound();

            // public void saveCatchMind(byte[] base64Data, String destinationPath, Long idx) throws IOException
            saveImageService.saveCatchMind(img, destinationPath, 1L);
        } else {
            log.error("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            log.error("roomIdx : " + roomIdx);
        }

    }

    @PostMapping("/settle")
    public ResponseEntity<int[]> settle(@RequestBody String roomIdx){

        // 점수 취합으로 게임 순위 부여
        // 점수 취합해서 유저한테 포인트 부여하기

        return null;
    }

}
