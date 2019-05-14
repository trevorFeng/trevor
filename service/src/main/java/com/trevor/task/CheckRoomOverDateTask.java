package com.trevor.task;

import com.trevor.dao.RoomPokeMapper;
import com.trevor.dao.RoomRecordMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

/**
 * @author trevor
 * @date 05/14/19 17:24
 */
@Component
public class CheckRoomOverDateTask implements ApplicationRunner {

    @Resource(name = "executor")
    private Executor executor;

    @Resource
    private RoomPokeMapper roomPokeMapper;

    @Resource
    private RoomRecordMapper roomRecordMapper;



    @Override
    public void run(ApplicationArguments args) throws Exception {
        Long currentTime = System.currentTimeMillis();

        executor.execute(() -> {

        });
    }
}
