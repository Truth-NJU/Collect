package com.collect.exception;

import com.collect.vo.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class MyException extends RuntimeException {
    HttpStatus httpStatus;
}
