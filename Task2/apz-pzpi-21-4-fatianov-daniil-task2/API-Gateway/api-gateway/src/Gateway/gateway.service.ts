import {Injectable} from "@nestjs/git common";
import {ConfigService} from "@nestjs/config";
import {createProxyMiddleware} from "http-proxy-middleware";
import {Request, Response} from "express";
import {NextFunction} from "http-proxy-middleware/dist/types";

@Injectable()
export class GatewayService {
    constructor(private readonly configService: ConfigService) {}

    proxy(req: Request, res: Response, serviceUrl: string, path: string) {
        const url = `${this.configService.get<string>(serviceUrl)}/${path}`;
        createProxyMiddleware({
            target: url,
            changeOrigin: true,
            pathRewrite: {
                [`^/${path}`]: '/'
            }
        })(req, res);
    }

}

