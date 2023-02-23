package org.example.management.system.utils;

import com.jianyue.lightning.result.Result;

import java.util.List;

public class ResultExt {

    public static Result<Void> success() {
        return Result.success(200,"success");
    }

    public static  <T> Result<T> success(T data) {
        return Result.success(200,"success",data);
    }
    public static <T> Result<T> success(List<T> data) {
        return Result.success(200,"success",data);
    }

    public static Result<Void> error() {
        return Result.error(400,"error");
    }

}
