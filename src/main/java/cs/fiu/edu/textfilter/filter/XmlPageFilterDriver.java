package cs.fiu.edu.textfilter.filter;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cs.fiu.edu.textfilter.custom.WholeFileInputFormat;

public class XmlPageFilterDriver extends Configured implements Tool {

	public static class FilterMapper extends
			Mapper<NullWritable, Text, Text, Text> {

		private Document document;
		private SAXReader saxReader = new SAXReader();
		private String query = "";
		private Configuration conf;

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			// TODO Auto-generated method stub
			conf = context.getConfiguration();
			query = conf.get("textfilter.query");
		}

		@Override
		protected void map(NullWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub

			try {
				document = saxReader.read(new StringReader(value.toString()));

				List list = document.selectNodes("//page");
				Iterator iter = list.iterator();

				while (iter.hasNext()) {
					Element page = (Element) iter.next();
					Element textEle = page.element("revision").element("text");
					Element timeEle = page.element("revision").element(
							"timestamp");
					if (textEle.getTextTrim().contains(query)) {
						context.write(new Text(timeEle.getText()), new Text(
								textEle.asXML()));
					}
				}

			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static class FilterReducer extends
			Reducer<Text, Text, NullWritable, Text> {

		private Document document;

		@Override
		protected void setup(Context context) throws IOException,
				InterruptedException {
			// TODO Auto-generated method stub
			document = DocumentHelper.createDocument();
		}
		

		@Override
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			for (Text value : values) {
				try {
					Element pageEle = (Element) DocumentHelper
							.parseText(value.toString()).getRootElement()
							.detach();
					document.add(pageEle);
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			context.write(NullWritable.get(), new Text(document.asXML()));
		}

	}

	public int run(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Configuration conf = getConf();
		Job filterJob = new Job(conf, "Wikipedia page filter job");
		filterJob.setJarByClass(XmlPageFilterDriver.class);
		filterJob.setMapperClass(FilterMapper.class);
		filterJob.setReducerClass(FilterReducer.class);

		FileSystem fs = FileSystem.get(conf);
		FileStatus[] list = fs.listStatus(new Path(args[0]));
		if (list != null) {
			for (FileStatus status : list) {
				FileInputFormat.addInputPath(filterJob, status.getPath());
			}
		}

		// WholeFileInputFormat
		filterJob.setInputFormatClass(WholeFileInputFormat.class);
		filterJob.setMapOutputKeyClass(Text.class);
		filterJob.setMapOutputValueClass(Text.class);

		filterJob.setNumReduceTasks(1);

		filterJob.waitForCompletion(true);
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int exit = ToolRunner.run(new XmlPageFilterDriver(), args);
		System.exit(exit);
	}
}
