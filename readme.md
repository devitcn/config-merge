# Config Merge

允许你用一个yaml文件做模板，另一个yaml文件做数据，输出替换后的新yaml

1. yaml模板内容替换
2. 将file://application.yml 替换成实际文件内容

## Build

    mvn package

## To Use

### By Code
    @Test
    public void single_merge() throws Exception {
    
        File out = new File("target/temp");
        out.mkdirs();
        
        merge.addPath(new File("src/test/resources").toPath());
        Map<String, Object> data = merge.parseYaml(new File("case1-data.yaml"));
        Map<String, Object> template = merge.parseYaml(new File("template.yaml"));
        merge.fillTemplateWithData(template, data);
        merge.writeYamlToFile(template, new File(out, "config.yml"));
        
        System.out.println("build yaml:");
        String yamlString = merge.dump(template);
        System.out.println(yamlString);
    }  

## License

Apache License Version 2.0  