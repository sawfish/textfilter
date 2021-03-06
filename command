Which City is Safer? A Bipartite-Graph Based Approach for Comparative Summarization.

miami 772
chicago 618
atlanta 545
boston 588
dallas 599
houston 568
los angles 607
new orleans 519
new york 605
philadelphia 685
san francisco 544
seattle 549


I chose six pair cities to do comparison
miami-chicago
miami-los+angeles
miami-philadelphia
chicago-los+angeles
chicago-philadelphia
los+angeles-philadelphia


following two mallet commands, replace two cities name for different city pair. 
The first corresponds to cityOne, and the second to cityTwo 

1. run InstanceEntityRemover
2. run FileMerger
3. bin/mallet import-file --input ~/U/workplace/textfilter/data/miami-chicago-disasters-NER.txt --print-output --remove-stopwords TRUE --output ~/U/workplace/textfilter/data/miami-chicago-disasters-STOP.mallet --label 2 --name 1 --data 3 --keep-sequence TRUE --extra-stopwords ~/U/workplace/textfilter/data/extra-stopwords.txt 
4. bin/mallet train-topics --input ~/U/workplace/textfilter/data/miami-chicago-disasters-STOP.mallet --num-topics 10 --output-topic-keys ~/U/workplace/textfilter/data/miami-chicago-topic-keys.txt --topic-word-weights-file ~/U/workplace/textfilter/data/miami-chicago-topic-word-weights.txt --output-doc-topics ~/U/workplace/textfilter/data/miami-chicago-doc-topics.txt
5. ComparisonBrain













bin/mallet import-file --input ~/U/workplace/textfilter/data/miami-chicago-disasters-NER.txt --print-output --remove-stopwords TRUE --output ~/U/workplace/textfilter/data/miami-chicago-disasters-STOP.mallet --label 2 --name 1 --data 3 --keep-sequence TRUE --extra-stopwords ~/U/workplace/textfilter/data/extra-stopwords.txt 
bin/mallet import-file --input ~/U/workplace/textfilter/data/miami-los+angeles-disasters-NER.txt --print-output --remove-stopwords TRUE --output ~/U/workplace/textfilter/data/miami-los+angeles-disasters-STOP.mallet --label 2 --name 1 --data 3 --keep-sequence TRUE --extra-stopwords ~/U/workplace/textfilter/data/extra-stopwords.txt 
bin/mallet import-file --input ~/U/workplace/textfilter/data/miami-philadelphia-disasters-NER.txt --print-output --remove-stopwords TRUE --output ~/U/workplace/textfilter/data/miami-philadelphia-disasters-STOP.mallet --label 2 --name 1 --data 3 --keep-sequence TRUE --extra-stopwords ~/U/workplace/textfilter/data/extra-stopwords.txt 
bin/mallet import-file --input ~/U/workplace/textfilter/data/chicago-philadelphia-disasters-NER.txt --print-output --remove-stopwords TRUE --output ~/U/workplace/textfilter/data/chicago-philadelphia-disasters-STOP.mallet --label 2 --name 1 --data 3 --keep-sequence TRUE --extra-stopwords ~/U/workplace/textfilter/data/extra-stopwords.txt 
bin/mallet import-file --input ~/U/workplace/textfilter/data/chicago-los+angeles-disasters-NER.txt --print-output --remove-stopwords TRUE --output ~/U/workplace/textfilter/data/chicago-los+angeles-disasters-STOP.mallet --label 2 --name 1 --data 3 --keep-sequence TRUE --extra-stopwords ~/U/workplace/textfilter/data/extra-stopwords.txt 
bin/mallet import-file --input ~/U/workplace/textfilter/data/los+angeles-philadelphia-disasters-NER.txt --print-output --remove-stopwords TRUE --output ~/U/workplace/textfilter/data/los+angeles-philadelphia-disasters-STOP.mallet --label 2 --name 1 --data 3 --keep-sequence TRUE --extra-stopwords ~/U/workplace/textfilter/data/extra-stopwords.txt 
bin/mallet train-topics --input ~/U/workplace/textfilter/data/miami-chicago-disasters-STOP.mallet --num-topics 10 --output-topic-keys ~/U/workplace/textfilter/data/miami-chicago-topic-keys.txt --topic-word-weights-file ~/U/workplace/textfilter/data/miami-chicago-topic-word-weights.txt --output-doc-topics ~/U/workplace/textfilter/data/miami-chicago-doc-topics.txt
bin/mallet train-topics --input ~/U/workplace/textfilter/data/miami-los+angeles-disasters-STOP.mallet --num-topics 10 --output-topic-keys ~/U/workplace/textfilter/data/miami-los+angeles-topic-keys.txt --topic-word-weights-file ~/U/workplace/textfilter/data/miami-los+angeles-topic-word-weights.txt --output-doc-topics ~/U/workplace/textfilter/data/miami-los+angeles-doc-topics.txt
bin/mallet train-topics --input ~/U/workplace/textfilter/data/miami-philadelphia-disasters-STOP.mallet --num-topics 10 --output-topic-keys ~/U/workplace/textfilter/data/miami-philadelphia-topic-keys.txt --topic-word-weights-file ~/U/workplace/textfilter/data/miami-philadelphia-topic-word-weights.txt --output-doc-topics ~/U/workplace/textfilter/data/miami-philadelphia-doc-topics.txt
bin/mallet train-topics --input ~/U/workplace/textfilter/data/chicago-philadelphia-disasters-STOP.mallet --num-topics 10 --output-topic-keys ~/U/workplace/textfilter/data/chicago-philadelphia-topic-keys.txt --topic-word-weights-file ~/U/workplace/textfilter/data/chicago-philadelphia-topic-word-weights.txt --output-doc-topics ~/U/workplace/textfilter/data/chicago-philadelphia-doc-topics.txt
bin/mallet train-topics --input ~/U/workplace/textfilter/data/chicago-los+angeles-disasters-STOP.mallet --num-topics 10 --output-topic-keys ~/U/workplace/textfilter/data/chicago-los+angeles-topic-keys.txt --topic-word-weights-file ~/U/workplace/textfilter/data/chicago-los+angeles-topic-word-weights.txt --output-doc-topics ~/U/workplace/textfilter/data/chicago-los+angeles-doc-topics.txt
bin/mallet train-topics --input ~/U/workplace/textfilter/data/los+angeles-philadelphia-disasters-STOP.mallet --num-topics 10 --output-topic-keys ~/U/workplace/textfilter/data/los+angeles-philadelphia-topic-keys.txt --topic-word-weights-file ~/U/workplace/textfilter/data/los+angeles-philadelphia-topic-word-weights.txt --output-doc-topics ~/U/workplace/textfilter/data/los+angeles-philadelphia-doc-topics.txt
