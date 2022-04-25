# x_mybatis-generator
mybatis生成xml,entity,mapper工具类，增加了DDL校验（注释校验，字段校验，索引校验），自动生成注释，实现Lombok注解，继承父类，生成serialVersionUID，可基于源码自行修改。

对ddl创建作出一些基本规范约束，方便团队管理。

引用druid(1.2.6)sql解析器和mybatis-generator(1.3.6) 


入口类
public class GeneratorApplication {
    public static void main(String[] args) throws IOException, XMLParserException, InvalidConfigurationException, SQLException, InterruptedException {
        String path = Objects.requireNonNull(
                GeneratorApplication.class.getClassLoader().getResource("generationConfig.xml")
        ).getPath();

        List<String> warnings = new ArrayList<String>();
        File configFile = new File(path);
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(true);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        // 校验ddl
        myBatisGenerator.generate(getDDLValidateCallback());
    }
    ...
}

结构说明

<img width="693" alt="image" src="https://user-images.githubusercontent.com/32291404/165118563-ef5f7ffe-52ca-449c-b6f5-a42f00a2217f.png">

若不满足ddl规则，则生成失败

<img width="764" alt="image" src="https://user-images.githubusercontent.com/32291404/165119367-1f3fe0f5-fbc9-49a8-8de0-5ca63fe27c5e.png">


生成效果

<img width="575" alt="image" src="https://user-images.githubusercontent.com/32291404/165119951-edb0e506-c3fa-4e7f-acae-405cdd407111.png">
<img width="446" alt="image" src="https://user-images.githubusercontent.com/32291404/165120018-23333b3d-009c-4f84-8e41-43c65f1bbf0f.png">


