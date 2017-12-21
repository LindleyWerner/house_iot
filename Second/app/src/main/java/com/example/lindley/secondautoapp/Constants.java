package com.example.lindley.secondautoapp;

/**
 * Created by lindley on 06/12/17.
 */

public interface Constants {
    int MESSAGE = 1;
    int CLOSE_WEBSOCKET = 2;
    int WEBSOCKET_FAILURE = 3;
    int STRING = 4;
    int START_RANGE = 1000;

    int TIMES_TO_TRY_CONNECT_TO_SERVER = 3;//must be >=1
    int TIME_SPLASH_SCREEN = 3000;
    int TIME_WAIT_ON_TRY_ANOTHER_SERVER_CONNECTION = 200;
}
