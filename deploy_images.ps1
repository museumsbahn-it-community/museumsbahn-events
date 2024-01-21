docker tag localhost/museum-railway-events-backend git.twatzl.eu/twatzl/museum-railway-events-backend
docker tag localhost/museum-railway-events-eventcollectors git.twatzl.eu/twatzl/museum-railway-events-eventcollectors
docker tag localhost/museum-railway-events-web git.twatzl.eu/twatzl/museum-railway-events-web

echo "pushing museum-railway-events backend"
docker push git.twatzl.eu/twatzl/museum-railway-events-backend
echo "pushing museum-railway-events eventcollectors"
docker push git.twatzl.eu/twatzl/museum-railway-events-eventcollectors
echo "pushing museum-railway-events web"
docker push git.twatzl.eu/twatzl/museum-railway-events-web
