# Server Essentials (Rest)

## Building
[1]. Install [GoLang](https://golang.org/) 

[2]. Download Server Essentials 

`git clone https://github.com/Wurmcraft/Server-Essentials.git`

`cd Server-Essentials`
  
`git checkout Rest/GoLang`
     
[3]. Install Dependencies 

`go get github.com/go-redis/redis`
    
`go get github.com/julienschmidt/httprouter`

[4]. Build

`go build -o (name)'

## Requirements to run (must be in the same directory as run)

- SSL Certificate (fullchain.pem, privkey.pem), Check out [Let's Encrypt](https://letsencrypt.org/)
- theme.css (Default provided, however it can be customized)

## Configuration

- Port (Main.go#17)
- SSL Certificate File / Locations (Main.go#18, Main.go#19)
