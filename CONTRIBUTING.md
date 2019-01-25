:wave:  Nejprve ti chci poděkovat, že máš zájem o vývoj Zonkoida!

Teď se pojďme pobavit o tom, proč potřebujeme nastavit nějaká pravidla a doporučení. Je to proto, aby se nám podařilo udržet kvalitu apky,
aby nedalo moc práce zamergovat pull requesty a zkrátka abychom si z toho neudělali peklo :)

## Pull Requesty ##
Cílem je dosáhnout následujícího:

- Udržet kvalitu Zonkoida
- Rychle opravit chyby, které trápí uživatele
- Nezatěžovat se navzájem konflikty změn a chybami
- Mít vývoj pod kontrolou

Před posláním Pull Requestu proto prosím o:
- Stručný a jasný popis změn
- Dodržení "štábní kultury" dodaného kódu
- Ideálně provedení code review a security checků, pokud je to pro danou změnu relevantní.


## Štábní kultura ##
Slouží k udržení čistoty kódu (můj původní kód taky není ideální, ale je rozdíl, když člověk vyvíjí sám a když je vývojářů víc)
a snadnějšího mergování změn. Nebudeme si nastavovat nějaká striktní pravidla, pojďme zkusit dodržet alespoň toto:

- Nevytvářet zbytečně nové package a zbytečně nepřesouvat již zařazené třídy / soubory. Pokud je to nutné, 
zkus se nejdřív domluvit s maintainerem.
- Charset je vždy UTF-8.
- Pro odsazení použij 4 mezery, nikoliv TAB. 
- Nepřeformátovávat (pretty code) existující kód, může být problém při merge.

Obecně se dá využít pravidel zde: https://google.github.io/styleguide/javaguide.html, ovšem taky ne vše dodržuji :)
