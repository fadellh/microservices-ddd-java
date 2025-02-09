d2 -w docs/high-level-arc.d2 out.svg
d2 -w docs/high-level-arc.d2 docs/high-level-arc.svg
d2 -w --layout=elk --sketch docs/high-level-arc.d2 docs/high-level-arc.svg
d2 -w docs/high-level-arc.d2 docs/high-level-arc.png


d2 -w --sketch docs/low-level-order-service.d2 docs/low-level-order-service.svg
d2 -w --sketch docs/low-level-order-service.d2 docs/low-level-order-service.png
d2 -w --sketch docs/low-level-inventory-service.d2 docs/low-level-inventory-service.svg
d2 -w --sketch docs/low-level-inventory-service.d2 docs/low-level-inventory-service.png

d2 -w --layout=elk --sketch docs/d2-lang-diagram/aggregate-domain.d2 docs/aggregate-domain.svg
d2 -w --layout=elk --sketch docs/d2-lang-diagram/aggregate-domain.d2 docs/aggregate-domain.png

d2 -w --layout=elk --sketch docs/d2-lang-diagram/value-object.d2 docs/value-object.svg
d2 -w --layout=elk --sketch docs/d2-lang-diagram/value-object.d2 docs/value-object.png



SonarQube

mwc-java % mvn clean verify sonar:sonar \
-Dsonar.projectKey=mwc-java \
-Dsonar.host.url=http://localhost:9000 \
-Dsonar.login=sqp_92183067deef7ab5d07a6a99501a306820fad200
