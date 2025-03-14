package ku.cs.restaurant.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class ImageServiceGo {
    private static final String url = "http://localhost:8000/images/";

    public byte[] getImage(String imageName) {
        RestTemplate rest = new RestTemplate();

        try {
            ResponseEntity<byte[]> response = rest.exchange(
                    url + imageName,
                    HttpMethod.GET,
                    new HttpEntity<>(null), byte[].class
            );

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
