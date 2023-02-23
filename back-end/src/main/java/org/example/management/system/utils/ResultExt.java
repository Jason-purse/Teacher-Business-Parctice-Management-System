package org.example.management.system.utils;

import com.jianyue.lightning.result.Result;

public class ResultExt {

    public static Result<Void> success() {
        return Result.success(200,"success");
    }

    public static Result<Void> error() {
        return Result.error(400,"error");
    }

}
