@protocol SVNHPlugin <NSObject>

@required

// by convention, and to reduce the likelihood of conflicts,
// a plugin's name should be a unique reversed FQDN
+ (NSString *) name;

@end
