package com.trung.amazonscraper.service;

public interface ScraperService {

    /**
     * Nhận một URL mục tiêu, thực thi việc cào dữ liệu và xuất kết quả ra file.
     *
     * @param targetUrl Đường dẫn URL của trang web cần cào dữ liệu (VD: link tìm kiếm Amazon)
     * @return Chuỗi thông báo kết quả (ví dụ: đường dẫn tới file CSV đã lưu)
     */
    String processScrapingAndExport(String targetUrl);

}