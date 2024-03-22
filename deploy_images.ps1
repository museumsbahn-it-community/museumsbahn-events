param(
    [string]
    $image_tag = "latest"
)

Write-Host "pushing images with tag: $image_tag"

docker tag localhost/museum-railway-events-backend git.twatzl.eu/twatzl/museum-railway-events-backend:$image_tag
docker tag localhost/museum-railway-events-eventcollectors git.twatzl.eu/twatzl/museum-railway-events-eventcollectors:$image_tag
docker tag localhost/museum-railway-events-web git.twatzl.eu/twatzl/museum-railway-events-web:$image_tag

echo "pushing museum-railway-events backend"
docker push git.twatzl.eu/twatzl/museum-railway-events-backend:$image_tag
echo "pushing museum-railway-events eventcollectors"
docker push git.twatzl.eu/twatzl/museum-railway-events-eventcollectors:$image_tag
echo "pushing museum-railway-events web"
docker push git.twatzl.eu/twatzl/museum-railway-events-web:$image_tag
