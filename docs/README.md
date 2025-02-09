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