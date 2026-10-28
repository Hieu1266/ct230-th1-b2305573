package vn.edu.bookstore.legacy.adapter.api;

import java.net.URI;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.edu.bookstore.legacy.adapter.soap.LegacyCatalogException;

@RestControllerAdvice
public class LegacyProblemHandler {

    @ExceptionHandler(InvalidRequestException.class)
    ProblemDetail onInvalid(InvalidRequestException e, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, String.valueOf(ProblemTypes.VALIDATION_FAILED),
                "Yêu cầu không hợp lệ", e.getMessage());
    }

    @ExceptionHandler(LegacyCatalogException.class)
    ProblemDetail onLegacy(LegacyCatalogException e, HttpServletRequest request) {
        ProblemDetail p = switch (e.kind()) {
            case NOT_FOUND -> problem(HttpStatus.NOT_FOUND, String.valueOf(ProblemTypes.BOOK_NOT_FOUND),
                    "Không tìm thấy sách", e.getMessage());
            case INVALID_REQUEST -> problem(HttpStatus.BAD_REQUEST, String.valueOf(ProblemTypes.VALIDATION_FAILED),
                    "Yêu cầu không hợp lệ", e.getMessage());
            case UPSTREAM_ERROR -> problem(HttpStatus.BAD_GATEWAY, String.valueOf(ProblemTypes.UPSTREAM_UNAVAILABLE),
                    "Hệ thống danh mục kế thừa báo lỗi", e.getMessage());
            case UPSTREAM_TIMEOUT -> problem(HttpStatus.GATEWAY_TIMEOUT, String.valueOf(ProblemTypes.UPSTREAM_UNAVAILABLE),
                    "Hệ thống danh mục kế thừa phản hồi quá chậm", "Hết thời gian chờ BookCatalogService");
            case UPSTREAM_UNAVAILABLE -> problem(HttpStatus.SERVICE_UNAVAILABLE, String.valueOf(ProblemTypes.UPSTREAM_UNAVAILABLE),
                    "Hệ thống danh mục kế thừa không truy cập được", "Không kết nối được BookCatalogService");
        };

        if (e.legacyCode() != null) {
            p.setProperty("legacyCode", e.legacyCode());
        }
        p.setInstance(URI.create(request.getRequestURI()));
        return p;
    }

    private ProblemDetail problem(HttpStatus status, String type, String title, String detail) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(status, detail);
        p.setType(URI.create(type));
        p.setTitle(title);
        return p;
    }
}
