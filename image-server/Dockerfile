FROM golang:1.23 AS builder

ENV IMAGE_DIR=/app/images

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod tidy && go mod download

COPY . .

RUN mkdir -p $IMAGE_DIR && chmod -R 777 $IMAGE_DIR

RUN go build -o image_server

EXPOSE 8000

CMD ["./image_server"]
